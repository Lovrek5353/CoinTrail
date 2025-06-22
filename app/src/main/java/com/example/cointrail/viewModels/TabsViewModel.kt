package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.example.cointrail.data.Tab
import com.example.cointrail.data.User
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Transaction
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TabsViewModel (
    private val repository: Repository
): ViewModel() {
    val user: StateFlow<User?> = repository.currentUser

    //Input states for Tab creation/editing
    var nameString by mutableStateOf("")
        private set
    var descriptionString by mutableStateOf("")
        private set
    var dueDateMillis by mutableStateOf<Long?>(null)
        private set
    var initialAmountString by mutableStateOf("")
        private set
    var interestRateString by mutableStateOf("")
        private set
    var monthlyPaymentString by mutableStateOf("")
        private set
    var startDateMillis by mutableStateOf<Long?>(null)
        private set
    var lenderString by mutableStateOf("")
        private set
    var statusString by mutableStateOf("")
        private set


    //Input states for Tab Transaction

    var transactionAmountString by mutableStateOf("")
        private set
    var transactionDescriptionString by mutableStateOf("")
        private set
    var transactionDateMillis by mutableStateOf<Long?>(null)
        private set
    var transactionCategoryID by mutableStateOf("")
        private set

    //For edit/update if needed (this tab state is used for observing a single tab)
    private val _tab = MutableStateFlow<Tab?>(null)
    val singleTab: MutableStateFlow<Tab?> = _tab

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    //Input handlers for Tab

    fun onNameInput(newName: String) {
        nameString = newName
    }

    fun onDescriptionInput(newDescription: String) {
        descriptionString = newDescription
    }

    fun onDueDateSelected(millis: Long) {
        dueDateMillis = millis
    }

    fun onInitialAmountInput(newAmount: String) {
        initialAmountString = newAmount
    }

    fun onInterestRateInput(newRate: String) {
        interestRateString = newRate
    }

    fun onMonthlyPaymentInput(newPayment: String) {
        monthlyPaymentString = newPayment
    }

    fun onStartDateSelected(millis: Long) {
        startDateMillis = millis
    }

    fun onLenderInput(newLender: String) {
        lenderString = newLender
    }

    fun onStatusInput(newStatus: String) {
        statusString = newStatus
    }

    //Tab Transaction input logic

    fun onAmountInputChange(newAmount: String){
        transactionAmountString = newAmount
    }

    fun onTransactionDateSelected(millis: Long?) {
        transactionDateMillis = millis
    }
    fun onTransactionDescriptionChange(newDescription: String) {
        transactionDescriptionString = newDescription
    }
    fun setTransactionCategory(categoryID: String) {
        transactionCategoryID = categoryID
    }

    //Tab Submission Logic

    fun onTabSubmit(){
        viewModelScope.launch {
            Log.d("TabsViewModel", "onTabSubmit called")
            try {
                //User part
                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    return@launch
                }

                //Validation
                if (nameString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Name cannot be empty"))
                    Log.d("TabsViewModel", "Name cannot be empty")
                    return@launch
                }
                if (descriptionString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Description cannot be empty"))
                    Log.d("TabsViewModel", "Description cannot be empty")
                    return@launch
                }
                if (dueDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Date cannot be empty"))
                    Log.d("TabsViewModel", "Date cannot be empty")
                    return@launch
                }
                if (initialAmountString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount cannot be empty"))
                    Log.d("TabsViewModel", "Amount cannot be empty")
                    return@launch
                }
                val amountValue = initialAmountString.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
                    Log.d("TabsViewModel", "Invalid amount")
                    return@launch
                }
                if (interestRateString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Interest rate cannot be empty"))
                    Log.d("TabsViewModel", "Interest rate cannot be empty")
                    return@launch
                }
                val interestRateValue = interestRateString.toDoubleOrNull()
                if (interestRateValue == null || interestRateValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid interest rate"))
                    Log.d("TabsViewModel", "Invalid interest rate")
                    return@launch
                }
                if (monthlyPaymentString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Monthly payment cannot be empty"))
                    Log.d("TabsViewModel", "Monthly payment cannot be empty")
                    return@launch
                }
                val monthlyPaymentValue = monthlyPaymentString.toDoubleOrNull()
                if (monthlyPaymentValue == null || monthlyPaymentValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid monthly payment"))
                    Log.d("TabsViewModel", "Invalid monthly payment")
                    return@launch
                }
                if (startDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Start date cannot be empty"))
                    Log.d("TabsViewModel", "Start date cannot be empty")
                    return@launch
                }
                if (lenderString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Lender cannot be empty"))
                    Log.d("TabsViewModel", "Lender cannot be empty")
                    return@launch
                }
                if (statusString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Status cannot be empty"))
                    Log.d("TabsViewModel", "Status cannot be empty")
                    return@launch
                }
                //Create final object
                val validatedTab = Tab(
                    name = nameString,
                    description = descriptionString,
                    dueDate = Timestamp(java.util.Date(dueDateMillis!!)),
                    userId = userId,
                    initialAmount = amountValue,
                    interestRate = interestRateValue,
                    monthlyPayment = monthlyPaymentValue,
                    startDate = Timestamp(java.util.Date(startDateMillis!!)),
                    lender = lenderString,
                    status = statusString,
                    outstandingBalance = 0.0
                )
                Log.d("TabsViewModel", "Tab: $validatedTab")
                repository.addTab(validatedTab)
                _eventFlow.emit(UiEvent.SubmissionSuccess)

                //Reset all fields
                nameString = ""
                descriptionString = ""
                dueDateMillis = null
                initialAmountString = ""
                interestRateString = ""
                monthlyPaymentString = ""
                startDateMillis = null
                lenderString = ""
                statusString = ""


            }
            catch (e: Exception){
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
            }
        }
    }


    //Tab Transaction Submission Logic
    fun onTransactionSubmit() {
        Log.d("TabsViewModel", "onTransactionSubmit called")
        viewModelScope.launch {
            try {
                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    Log.d("TabsViewModel", "User not logged in")
                    return@launch
                }

                //Validation

                if (transactionAmountString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount cannot be empty"))
                    Log.d("TabsViewModel", "Amount cannot be empty")
                    return@launch
                }
                val amountValue = transactionAmountString.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
                    Log.d("TabsViewModel", "Invalid amount")
                    return@launch
                }
                if (transactionDescriptionString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Description cannot be empty"))
                    Log.d("TabsViewModel", "Description cannot be empty")
                    return@launch
                }
                if (transactionDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Date cannot be empty"))
                    Log.d("TabsViewModel", "Date cannot be empty")
                    return@launch
                }
                if (transactionCategoryID.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Category cannot be empty"))
                    Log.d("TabsViewModel", "Category cannot be empty")
                    return@launch
                }

                //Calculate new Tab information

                val currentTab = _tab.value
                val tabId = currentTab?.id
                if (tabId.isNullOrEmpty()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("No tab selected for transaction"))
                    Log.d("TabsViewModel", "No tab selected for transaction")
                    return@launch
                }

                val oldbalance= currentTab.outstandingBalance
                val newbalance = oldbalance + amountValue

                //Create transaction object
                val transactionToSave = Transaction(
                    userID = userId,
                    amount = amountValue,
                    date = Timestamp(java.util.Date(transactionDateMillis!!)),
                    description = transactionDescriptionString,
                    categoryId = transactionCategoryID
                )

                //Add transaction
                repository.addTransaction(transactionToSave)
                Log.d("TabsViewModel", "Transaction added: $transactionToSave")

                //Update balance
                repository.updateTabBalance(tabId, newbalance)
                Log.d("TabsViewModel", "Balance updated for tab $tabId")

                _eventFlow.emit(UiEvent.SubmissionSuccess)

                //Reset all transaction-related fields
                transactionAmountString = ""
                transactionDescriptionString = ""
                transactionDateMillis = null
                transactionCategoryID = ""
            }
            catch (e: Exception){
                _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
            }
        }
    }

    fun fetchTabs(): SharedFlow<List<Tab>> {
        return repository.getTabs()
    }

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun observeTransactions(tabId: String) {
        viewModelScope.launch {
            repository.getCategoryTransactions(tabId)
                .collect { txList ->
                    _transactions.value = txList
                }
        }
    }

    fun fetchTab(tabId: String) {
        viewModelScope.launch {
            repository.getTab(tabId)
                .collect { tab ->
                    _tab.value = tab
                }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SubmissionSuccess : UiEvent()
    }
}