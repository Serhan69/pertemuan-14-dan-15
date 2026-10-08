package com.industri.fleettrack.data.repository

import com.industri.fleettrack.data.local.dao.DeliveryDao
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.local.entity.SyncQueueEntity
import com.industri.fleettrack.data.remote.api.CourierApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class DeliveryRepository(
    private val deliveryDao: DeliveryDao,
    private val apiService: CourierApiService
) {

    fun getOrdersFlow(query: String): Flow<List<DeliveryOrderEntity>> {
        return if (query.isBlank()) {
            deliveryDao.getAllOrdersFlow()
        } else {
            deliveryDao.searchOrdersFlow(query)
        }
    }

    suspend fun markAsDelivered(orderId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Update local database immediately (Offline-First)
            deliveryDao.updateOrderStatus(orderId, "DELIVERED", isSynced = false)
            
            // 2. Add to sync queue
            deliveryDao.insertSyncQueue(
                SyncQueueEntity(
                    orderId = orderId,
                    targetStatus = "DELIVERED",
                    notes = "Marked delivered by courier offline/online"
                )
            )

            // 3. Attempt sync with server
            syncPendingChanges()

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error marking order as delivered for ID: $orderId")
            Result.failure(e)
        }
    }

    suspend fun refreshManifest(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Try fetching remote manifest
            val response = apiService.getTodayManifest()
            if (response.isSuccessful && response.body() != null) {
                val remoteOrders = response.body()!!
                // Upsert into local database
                deliveryDao.upsertOrders(remoteOrders)
            } else {
                Timber.w("Remote manifest fetch failed or returned non-success: ${response.code()}")
            }

            // 2. Process pending sync queue
            syncPendingChanges()

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.w(e, "Network unavailable or failed during refresh. Using local cache.")
            // Do not fail user action; local is source of truth
            Result.success(Unit)
        }
    }

    private suspend fun syncPendingChanges() {
        val queueItems = deliveryDao.getAllSyncQueue()
        for (item in queueItems) {
            try {
                val response = apiService.updateDeliveryStatus(item.orderId, item.targetStatus)
                if (response.isSuccessful) {
                    // Update order as synced in local DB
                    deliveryDao.updateOrderStatus(item.orderId, item.targetStatus, isSynced = true)
                    // Remove from queue
                    deliveryDao.deleteSyncQueue(item.queueId)
                    Timber.d("Successfully synced order ${item.orderId} status ${item.targetStatus}")
                }
            } catch (e: Exception) {
                Timber.w("Failed to sync queue item ${item.queueId} for order ${item.orderId}: ${e.localizedMessage}")
                // Keep in queue for future retry
                break 
            }
        }
    }
}
