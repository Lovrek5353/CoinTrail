package com.example.cointrail.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.AssetSearch
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StocksViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _searchResults=MutableStateFlow<List<AssetSearch>>(emptyList())
    val searchResults: StateFlow<List<AssetSearch>> = _searchResults

    fun searchAssets(query: String) {
        viewModelScope.launch {
            repository.searchAssets(query)
                .collect { results ->
                    _searchResults.value = results
                }
        }
    }
}

sealed class ApiResult {
    object Loading : ApiResult()
    data class Success(val data: String) : ApiResult()
    data class Error(val message: String) : ApiResult()
    object Idle : ApiResult() // Initial state
}