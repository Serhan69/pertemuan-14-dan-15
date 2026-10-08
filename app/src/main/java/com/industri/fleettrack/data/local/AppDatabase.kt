package com.industri.fleettrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.industri.fleettrack.data.local.dao.DeliveryDao
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(entities = [DeliveryOrderEntity::class, SyncQueueEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deliveryDao(): DeliveryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fleet_track_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateSeedData(database.deliveryDao())
                    }
                }
            }

            suspend fun populateSeedData(dao: DeliveryDao) {
                if (dao.getOrderCount() == 0) {
                    val seedOrders = listOf(
                        DeliveryOrderEntity(
                            orderId = "ORD-2026-8801",
                            trackingNumber = "EXP-90021-JKT",
                            recipientName = "Ibu Ratna Dewi",
                            recipientPhone = "081234567890",
                            destinationAddress = "Jl. Melati Raya No. 45, Harjamukti, Cirebon",
                            codAmount = 125000.0,
                            deliveryStatus = "PENDING",
                            isSynced = true
                        ),
                        DeliveryOrderEntity(
                            orderId = "ORD-2026-8802",
                            trackingNumber = "EXP-90022-BDG",
                            recipientName = "Pak Bambang Sutrisno",
                            recipientPhone = "081398765432",
                            destinationAddress = "Jl. Kartini No. 12, Kejaksan, Cirebon",
                            codAmount = 0.0,
                            deliveryStatus = "IN_DELIVERY",
                            isSynced = true
                        ),
                        DeliveryOrderEntity(
                            orderId = "ORD-2026-8803",
                            trackingNumber = "EXP-90023-CRB",
                            recipientName = "Sdr. Dimas Prasetyo",
                            recipientPhone = "085712349988",
                            destinationAddress = "Perum Citra Garden Blok B-3, Kedawung",
                            codAmount = 75000.0,
                            deliveryStatus = "DELIVERED",
                            isSynced = true
                        )
                    )
                    dao.upsertOrders(seedOrders)
                    Timber.d("Seeded initial delivery orders successfully.")
                }
            }
        }
    }
}
