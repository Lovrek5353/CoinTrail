package com.example.cointrail.viewModels

import androidx.compose.ui.graphics.vector.EmptyPath
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cointrail.data.AssetHistory
import com.example.cointrail.data.AssetSearch
import com.example.cointrail.data.Stock
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StocksViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _assetDetails=MutableStateFlow<Stock>(Stock())
    val assetDetails: StateFlow<Stock> = _assetDetails

    // Expose search results as StateFlow, debounced and reactive
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<AssetSearch>> = _searchQuery
        .debounce(500) // Wait 500ms after last input
        .filter { it.isNotBlank() } // Ignore empty queries if you want
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.searchAssets(query)
                .catch { emit(emptyList()) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }


    private val _stockState = MutableStateFlow<Stock?>(null)
    val stockState: StateFlow<Stock?> = _stockState

    fun fetchStockDetails(symbol: String, type: String) {
        repository.fetchAssetDetails(symbol, type)
            .onEach { stock ->
                _stockState.value = stock
            }
            .launchIn(viewModelScope)
    }

    private val _stockHistory=MutableStateFlow<List<AssetHistory>>(emptyList())
    val stockHistory: StateFlow<List<AssetHistory>> = _stockHistory

    fun fetchStockHistory(symbol: String) {
        repository.fetchAssetHistory(symbol)
            .onEach { history ->
                _stockHistory.value = history
            }
            .launchIn(viewModelScope)
    }
}
