package com.industri.fleettrack.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "delivery_orders",
    indices = [Index(value = ["tracking_number"], unique = true)]
)
data class DeliveryOrderEntity(
    @PrimaryKey
    @ColumnInfo(name = "order_id")
    @SerializedName("order_id")
    val orderId: String,

    @ColumnInfo(name = "tracking_number")
    @SerializedName("tracking_number")
    val trackingNumber: String,

    @ColumnInfo(name = "recipient_name")
    @SerializedName("recipient_name")
    val recipientName: String,

    @ColumnInfo(name = "recipient_phone")
    @SerializedName("recipient_phone")
    val recipientPhone: String,

    @ColumnInfo(name = "destination_address")
    @SerializedName("destination_address")
    val destinationAddress: String,

    @ColumnInfo(name = "cod_amount")
    @SerializedName("cod_amount")
    val codAmount: Double = 0.0,

    @ColumnInfo(name = "delivery_status")
    @SerializedName("status")
    val deliveryStatus: String, // PENDING, IN_DELIVERY, DELIVERED, FAILED

    @ColumnInfo(name = "failure_reason")
    val failureReason: String? = null,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = true,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "queue_id")
    val queueId: Long = 0L,

    @ColumnInfo(name = "order_id")
    val orderId: String,

    @ColumnInfo(name = "target_status")
    val targetStatus: String,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
