package com.industri.fleettrack.ui.manifest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import com.industri.fleettrack.data.repository.DeliveryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ManifestViewModel(
    private val repository: DeliveryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val ordersFlow: StateFlow<List<DeliveryOrderEntity>> = _searchQuery
        .flatMapLatest { query ->
            repository.getOrdersFlow(query)
        }.let { flow ->
            val stateFlow = MutableStateFlow<List<DeliveryOrderEntity>>(emptyList())
            viewModelScope.launch {
                flow.collect { orders ->
                    stateFlow.value = orders
                }
            }
            stateFlow.asStateFlow()
        }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.refreshManifest()
            _isLoading.value = false
            if (result.isFailure) {
                _uiMessage.value = "Gagal sinkronisasi remote: ${result.exceptionOrNull()?.localizedMessage}"
            } else {
                _uiMessage.value = "Manifest berhasil diperbarui."
            }
        }
    }

    fun markAsDelivered(orderId: String) {
        viewModelScope.launch {
            val result = repository.markAsDelivered(orderId)
            if (result.isSuccess) {
                _uiMessage.value = "Paket berhasil ditandai terkirim."
            } else {
                _uiMessage.value = "Gagal memperbarui status paket."
            }
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    class Factory(private val repository: DeliveryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ManifestViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManifestViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
