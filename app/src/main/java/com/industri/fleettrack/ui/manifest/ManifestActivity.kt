package com.industri.fleettrack.ui.manifest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.industri.fleettrack.data.local.AppDatabase
import com.industri.fleettrack.data.remote.api.CourierApiService
import com.industri.fleettrack.data.repository.DeliveryRepository
import com.industri.fleettrack.databinding.ActivityManifestBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManifestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManifestBinding

    private val viewModel: ManifestViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val apiService = CourierApiService.create()
        val repository = DeliveryRepository(database.deliveryDao(), apiService)
        ManifestViewModel.Factory(repository)
    }

    private lateinit var adapter: DeliveryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManifestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupSearch()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = DeliveryListAdapter { order ->
            viewModel.markAsDelivered(order.orderId)
        }
        binding.rvManifest.layoutManager = LinearLayoutManager(this)
        binding.rvManifest.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchManifest.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.ordersFlow.collectLatest { orders ->
                        adapter.submitList(orders)
                        if (orders.isEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collectLatest { loading ->
                        binding.swipeRefresh.isRefreshing = loading
                        binding.pbMainLoading.visibility = if (loading && adapter.itemCount == 0) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.uiMessage.collectLatest { msg ->
                        msg?.let {
                            Toast.makeText(this@ManifestActivity, it, Toast.LENGTH_SHORT).show()
                            viewModel.clearMessage()
                        }
                    }
                }
            }
        }
    }
}
