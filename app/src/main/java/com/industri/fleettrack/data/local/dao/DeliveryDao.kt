package com.industri.fleettrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {

    @Query("SELECT * FROM delivery_orders ORDER BY updated_at DESC")
    fun getAllOrdersFlow(): Flow<List<DeliveryOrderEntity>>

    @Query("""
        SELECT * FROM delivery_orders 
        WHERE tracking_number LIKE '%' || :query || '%' 
           OR recipient_name LIKE '%' || :query || '%' 
           OR destination_address LIKE '%' || :query || '%'
        ORDER BY updated_at DESC
    """)
    fun searchOrdersFlow(query: String): Flow<List<DeliveryOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrders(orders: List<DeliveryOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOrder(order: DeliveryOrderEntity)

    @Query("UPDATE delivery_orders SET delivery_status = :status, is_synced = :isSynced, updated_at = :updatedAt WHERE order_id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, isSynced: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM delivery_orders")
    suspend fun getOrderCount(): Int

    // Sync Queue methods
    @Query("SELECT * FROM sync_queue ORDER BY timestamp ASC")
    suspend fun getAllSyncQueue(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncQueue(queue: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE queue_id = :queueId")
    suspend fun deleteSyncQueue(queueId: Long)
}
