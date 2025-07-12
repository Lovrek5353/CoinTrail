package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Transaction
import com.example.cointrail.repository.Repository
import com.example.cointrail.viewModels.TabsViewModel.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Timestamp


class TransactionViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    private var currentJob: Job? = null
    private var lastLoadParams: Pair<String, String>? = null

    //Update transaction input handlers

    var transactionAmountString by mutableStateOf("")
        private set

    var transactionDescriptionString by mutableStateOf("")
        private set
    var transactionDateMillis by mutableStateOf<Long?>(null)
        private set

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
                    .distinctUntilChanged()
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
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            val result = repository.deleteTransaction(transactionId)
            if (result.isSuccess) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Deleted successfully"))
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar("Delete failed"))
            }
        }
    }
    fun updateBalanceAfterDeletion(documentId: String, transactionAmount: Double) {
        viewModelScope.launch {
            try {
                repository.updateBalanceAfterDeletion(documentId, transactionAmount)
                // Optionally emit a success event or update state
            } catch (e: Exception) {
                // Optionally emit an error event or update state
            }
        }
    }
    fun loadSingleTransaction(transactionId: String) {
        currentJob?.cancel()
        _loading.value = true
        _transaction.value = null

        currentJob = viewModelScope.launch {
            try {
                repository.getTransaction(transactionId).collect { transaction ->
                    _transaction.value = transaction
                    transactionAmountString = transaction.amount.toString()
                    transactionDescriptionString = transaction.description ?: ""
                    transactionDateMillis = transaction.date?.toDate()?.time
                    _loading.value = false
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading single transaction", e)
                _loading.value = false
                _transaction.value = null
                _eventFlow.emit(UiEvent.ShowSnackbar("Error loading transaction"))
            }
        }
    }

    fun onAmountStringUpdate(newAmount: String) {
        transactionAmountString=newAmount
    }
    fun onDescriptionStringUpdate(newDescription: String) {
        transactionDescriptionString=newDescription
    }
    fun onDateSelected(millis: Long) {
        transactionDateMillis = millis
    }

    fun updateTransaction() {
        Log.d("TransactionViewModel", "updateTransaction called")
        viewModelScope.launch {
            try {
                // Validation
                if (transactionAmountString.isEmpty()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Please enter an amount"))
                    Log.d("TransactionViewModel", "Amount is empty")
                    return@launch
                }
                val amountValue = transactionAmountString.toDoubleOrNull()
                if (amountValue == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Please enter a valid amount"))
                    Log.d("TransactionViewModel", "Amount is not a number")
                    return@launch
                }
                if (transactionDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Please select a date"))
                    Log.d("TransactionViewModel", "Date is null")
                    return@launch
                }

                // Use the loaded transaction as the base
                val baseTransaction = _transaction.value
                if (baseTransaction == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("No transaction loaded"))
                    Log.d("TransactionViewModel", "No transaction loaded")
                    return@launch
                }

                // Create a copy with updated fields
                val updatedTransaction = baseTransaction.copy(
                    amount = amountValue,
                    date = Timestamp(java.util.Date(transactionDateMillis!!)),
                    description = transactionDescriptionString
                    // Add other fields here if you allow editing them
                )

                // Update transaction in repository
                repository.updateTransaction(updatedTransaction)
                _eventFlow.emit(UiEvent.SubmissionSuccess)

                // Reset all fields
                transactionAmountString = ""
                transactionDescriptionString = ""
                transactionDateMillis = null

            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error updating transaction", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Error updating transaction"))
            }
        }
    }

    fun updateBalanceAfterUpdate(documentId: String, oldTransactionAmount: Double, transactionAmount: Double ){
        viewModelScope.launch {
            try{
                repository.updateBalanceAfterTransactionEdit(documentId, oldTransactionAmount, transactionAmount )
            }
            catch (e: Exception) {
                // Optionally emit an error event or update state
            }
        }
    }



    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SubmissionSuccess : UiEvent()
    }
}
