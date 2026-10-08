package com.industri.fleettrack.ui.manifest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.databinding.ItemDeliveryOrderBinding
import java.text.NumberFormat
import java.util.Locale

class DeliveryListAdapter(
    private val onDeliverClicked: (DeliveryOrderEntity) -> Unit
) : ListAdapter<DeliveryOrderEntity, DeliveryListAdapter.DeliveryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemDeliveryOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DeliveryViewHolder(binding, onDeliverClicked)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DeliveryViewHolder(
        private val binding: ItemDeliveryOrderBinding,
        private val onDeliverClicked: (DeliveryOrderEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeliveryOrderEntity) {
            binding.tvTrackingNo.text = item.trackingNumber
            binding.tvRecipient.text = "${item.recipientName} (${item.recipientPhone})"
            binding.tvAddress.text = item.destinationAddress
            
            val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvCodAmount.text = rupiahFormat.format(item.codAmount)

            // Status Badge
            binding.tvStatusBadge.text = item.deliveryStatus
            when (item.deliveryStatus) {
                "PENDING" -> {
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#FFF9C4")) // Amber light
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#F57F17"))
                    binding.btnActionDeliver.isEnabled = true
                    binding.btnActionDeliver.text = "Tandai Terkirim"
                }
                "IN_DELIVERY" -> {
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#E3F2FD")) // Blue light
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#1565C0"))
                    binding.btnActionDeliver.isEnabled = true
                    binding.btnActionDeliver.text = "Tandai Terkirim"
                }
                "DELIVERED" -> {
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#E8F5E9")) // Green light
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#2E7D32"))
                    binding.btnActionDeliver.isEnabled = false
                    binding.btnActionDeliver.text = "Selesai (Delivered)"
                }
                "FAILED" -> {
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#FFEBEE")) // Red light
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#C62828"))
                    binding.btnActionDeliver.isEnabled = true
                    binding.btnActionDeliver.text = "Coba Lagi / Kirim Ulang"
                }
                else -> {
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#EEEEEE"))
                    binding.tvStatusBadge.setTextColor(Color.DKGRAY)
                    binding.btnActionDeliver.isEnabled = true
                    binding.btnActionDeliver.text = "Tandai Terkirim"
                }
            }

            binding.btnActionDeliver.setOnClickListener {
                onDeliverClicked(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DeliveryOrderEntity>() {
        override fun areItemsTheSame(oldItem: DeliveryOrderEntity, newItem: DeliveryOrderEntity): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: DeliveryOrderEntity, newItem: DeliveryOrderEntity): Boolean {
            return oldItem == newItem
        }
    }
}
