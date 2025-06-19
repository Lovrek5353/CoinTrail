package com.example.cointrail.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Transaction
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch



class TransactionViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private var currentJob: Job? = null
    private var lastLoadParams: Pair<String, String>? = null

    fun loadTransaction(categoryId: String, transactionId: String) {
        val params = Pair(categoryId, transactionId)
        if (params == lastLoadParams && currentJob?.isActive == true) {
            Log.d("TransactionViewModel", "Skipping duplicate loadTransaction call for $params")
            return
        }

        lastLoadParams = params
        currentJob?.cancel()
        Log.d("TransactionViewModel", "Initiating new load for $params. Setting _transaction to null.")
        _transaction.value = null // Reset to null when starting new load

        currentJob = viewModelScope.launch {
            try {
                repository.getCategoryTransactions(categoryId)
                    .distinctUntilChanged() // This works on the List<Transaction>
                    .collect { txList ->
                        val foundTransaction = txList.find { it.id == transactionId }

                        Log.d("TransactionViewModel", "Collected list of size ${txList.size}. Found transaction: $foundTransaction")

                        if (foundTransaction == null) {
                            Log.w("TransactionViewModel", "!!! WARNING: Specific transaction '$transactionId' not found in the collected list for category '$categoryId'. List contents: ${txList.map { it.id }}")
                        }

                        _transaction.value = foundTransaction
                        _loading.value = false
                    }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading transaction", e)
                _loading.value = false
                _transaction.value = null
            }
        }
    }
}
