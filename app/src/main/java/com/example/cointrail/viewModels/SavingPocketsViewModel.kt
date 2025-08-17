package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.repository.Repository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class SavingPocketsViewModel(
    private val repository: Repository
) : ViewModel() {
    val user: StateFlow<User?> = repository.currentUser

    // Input states for SavingPocket creation/editing
    var nameString by mutableStateOf("")
        private set
    var descriptionString by mutableStateOf("")
        private set
    var targetAmountString by mutableStateOf("")
        private set
    var selectedDateMillis by mutableStateOf<Long?>(null) // Date for SavingPocket
        private set

    // Input states for Transaction - all independent
    var transactionAmountString by mutableStateOf("")
        private set
    var transactionDescriptionString by mutableStateOf("")
        private set
    var transactionDateMillis by mutableStateOf<Long?>(null) // Date for Transaction
        private set
    var transactionCategoryID by mutableStateOf("") // Added for category - assuming it's selected by user
        private set

    // For edit/update if needed (this savingPocket state is used for observing a single pocket)
    private val _savingPocket = MutableStateFlow<SavingPocket?>(null)
    val singleSavingPocket: MutableStateFlow<SavingPocket?> = _savingPocket

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // --- Input Handlers for SavingPocket ---
    fun onNameInput(newName: String) {
        nameString = newName
    }

    fun onDescriptionInput(newDescription: String) {
        descriptionString = newDescription
    }

    fun onTargetAmountInput(newAmount: String) {
        targetAmountString = newAmount
    }

    fun onDateSelected(millis: Long) {
        selectedDateMillis = millis
    }

    // --- Transaction Input Handlers ---
    fun onAmountInputChange(newAmount: String) {
        transactionAmountString = newAmount
    }

    fun onTransactionDateSelected(millis: Long?) {
        transactionDateMillis = millis
    }

    fun onTransactionDescriptionChange(newDescription: String) {
        transactionDescriptionString = newDescription
    }

    fun setTransactionCategory(categoryID: String) {
        transactionCategoryID = categoryID // Update the independent state for category
    }

    // --- SavingPocket Submission Logic (No Change) ---
    fun onSubmit() {
        Log.d("SavingPocketsViewModel", "onSubmit called")
        viewModelScope.launch {
            try {
                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    return@launch
                }

                // --- Validation ---
                if (nameString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Name cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Name cannot be empty")
                    return@launch
                }
                if (descriptionString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Description cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Description cannot be empty")
                    return@launch
                }
                if (targetAmountString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Amount cannot be empty")
                    return@launch
                }
                val amountValue = targetAmountString.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
                    Log.d("SavingPocketsViewModel", "Invalid amount")
                    return@launch
                }
                if (selectedDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Date cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Date cannot be empty")
                    return@launch
                }

                // --- Create Final Object ---
                val validatedPocket = SavingPocket(
                    userID = userId,
                    name = nameString,
                    description = descriptionString,
                    targetAmount = amountValue,
                    targetDate = Timestamp(Date(selectedDateMillis!!))
                )

                Log.d("SavingPocketsViewModel", "Saving pocket: $validatedPocket")
                repository.addSavingPocket(validatedPocket)
                _eventFlow.emit(UiEvent.SubmissionSuccess)

                // --- Reset all fields ---
                nameString = ""
                descriptionString = ""
                targetAmountString = ""
                selectedDateMillis = null
                // No need to reset 'savingPocket' here, as it's for fetching a single item, not input.
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
            }
        }
    }

    // --- Transaction Submission Logic (Refactored) ---
    fun onTransactionSubmit() {
        Log.d("SavingPocketsViewModel", "onTransactionSubmit called")
        viewModelScope.launch {
            try {
                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    Log.d("SavingPocketsViewModel", "User not logged in")
                    return@launch
                }

                // --- Validation ---
                if (transactionAmountString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Amount cannot be empty")
                    return@launch
                }
                val amountValue = transactionAmountString.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
                    Log.d("SavingPocketsViewModel", "Invalid amount")
                    return@launch
                }
                if (transactionDescriptionString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Description cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Description cannot be empty")
                    return@launch
                }
                if (transactionDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Date cannot be empty"))
                    Log.d("SavingPocketsViewModel", "Date cannot be empty")
                    return@launch
                }
                // Optional: Add validation for transactionCategoryID if it's mandatory
                // if (transactionCategoryID.isBlank()) {
                //     _eventFlow.emit(UiEvent.ShowSnackbar("Category cannot be empty"))
                //     return@launch
                // }


                val currentSavingPocket =
                    _savingPocket.value // Get the currently observed saving pocket
                Log.d("CurrentSavingPocket", currentSavingPocket.toString())
                val savingPocketId = currentSavingPocket?.id
                if (savingPocketId.isNullOrEmpty()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("No saving pocket selected for transaction"))
                    Log.d("SavingPocketsViewModel", "No saving pocket selected for transaction")
                    return@launch
                }

                // Calculate new balance
                val oldBalance =
                    currentSavingPocket.balance // Use the balance from the observed pocket
                val newBalance = oldBalance + amountValue

                // Create transaction object directly from the individual states
                val transactionToSave = Transaction(
                    userID = userId,
                    amount = amountValue,
                    date = Timestamp(Date(transactionDateMillis!!)), // Convert Long to Timestamp here
                    description = transactionDescriptionString,
                    categoryId = transactionCategoryID, // Use the dedicated category state
                    type= TransactionType.SAVINGS
                )
                // 1. Add transaction
                repository.addSavingPocketTransaction(transactionToSave)
                Log.d("SavingPocketsViewModel", "Transaction added: $transactionToSave")

                // 2. Update balance (pass new balance)
                repository.updateSavingPocketBalance(savingPocketId, newBalance)
                Log.d("SavingPocketsViewModel", "Balance updated for saving pocket $savingPocketId")

                _eventFlow.emit(UiEvent.SubmissionSuccess)

                // Reset all transaction-related fields
                transactionAmountString = ""
                transactionDescriptionString = ""
                transactionDateMillis = null
                transactionCategoryID = "" // Reset category ID as well
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
            }
        }
    }


    // --- Fetching and Observing Data ---
    fun fetchSavingPockets(): SharedFlow<List<SavingPocket>> {
        return repository.getSavingPockets()
    }

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun observeTransactions(savingPocketId: String) {
        viewModelScope.launch {
            repository.getCategoryTransactions(savingPocketId) // Assuming this is correct for transactions within a pocket
                .collect { txList ->
                    _transactions.value = txList
                }
        }
    }

    fun fetchSavingPocket(savingPocketId: String) {
        viewModelScope.launch {
            repository.getSavingPocket(savingPocketId)
                .collect { savingPocket ->
                    _savingPocket.value = savingPocket
                }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SubmissionSuccess : UiEvent()
    }
}
