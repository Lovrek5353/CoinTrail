package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.AssetHistory
import com.example.cointrail.data.AssetSearch
import com.example.cointrail.data.Stock
import com.example.cointrail.repository.Repository
import com.google.firebase.Timestamp
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

    // UI event flow for Snackbar/navigation
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var amountString by mutableStateOf("")
        private set

    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks

    init {
        // Collect from stocksSharedFlow from repository
        viewModelScope.launch {
            repository.stocksSharedFlow.collect { stockList ->
                _stocks.value = stockList
            }
        }
    }
    // Expose search results as StateFlow, debounced and reactive
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<AssetSearch>> = _searchQuery
        .debounce(500)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.searchAssets(query)
                .catch { emit(emptyList()) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onAmountInput(newAmount: String) {
        amountString = newAmount
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

    fun onStockAdd(){
        viewModelScope.launch{
            try{
                val currentUser = repository.currentUser.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    return@launch
                }

                //Validation
                if (amountString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount cannot be empty"))
                    return@launch
                }
                val amountValue = amountString.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
                    return@launch
                }
                //Create stock record to store in database
                val stockToSave=Stock(
                    name= stockState.value!!.name,
                    symbol = stockState.value!!.symbol,
                    originalPrice = stockState.value!!.currentPrice,
                    currentPrice = stockState.value!!.currentPrice,
                    amount=amountValue,
                    currentStockPrice = stockState.value!!.currentPrice*amountValue,
                    currency = stockState.value!!.currency,
                    netChange = stockState.value!!.netChange,
                    deltaIndicator = stockState.value!!.deltaIndicator,
                    exchange = stockState.value!!.exchange,
                    purchaseDate = Timestamp.now(),
                    userID=userId
                )
                repository.addStockToDB(stockToSave)
                Log.d("StocksViewModel", "Stock saved: $stockToSave")
                _eventFlow.emit(UiEvent.SubmissionSuccess)
            }
            catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
            }
        }
    }
    private val _currentStock = MutableStateFlow<Stock?>(null)
    val currentStock: StateFlow<Stock?> = _currentStock

    // Call this function with the stock ID you want to observe
    fun loadStock(stockID: String) {
        viewModelScope.launch {
            repository.getStock(stockID).collect { stock ->
                _currentStock.value = stock
            }
        }
    }

    fun updateStockPrice(stockID: String, newPrice: Double) {
        viewModelScope.launch {
            try {
                repository.updateStockInfo(stockID, newPrice)
            } catch (e: Exception) {
            }
        }
    }

    val favoriteAssets: StateFlow<List<AssetSearch>> =
        repository.getFavorites()
            .stateIn(
                scope = viewModelScope, // Cancels when ViewModel is cleared
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addToFavorite(stock: AssetSearch)
    {
        viewModelScope.launch {
            repository.addToFavorite(stock)
        }
    }
    fun removeFromFavorite(stock: AssetSearch)
    {
        viewModelScope.launch {
            repository.removeFromFavorite(stock)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SubmissionSuccess : UiEvent()
    }
}
