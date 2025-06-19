package com.example.cointrail.viewModels

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

    fun fetchTabs(): SharedFlow<List<Tab>> {
        return repository.getTabs()
    }

//    var tab by mutableStateOf(Tab())
//        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onSubmit() {
        viewModelScope.launch {
            try {

                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    return@launch
                }
//
//                val tabToSave = tab.copy(userId = userId)

//                if (tabToSave.name.isBlank()) {
//                    _eventFlow.emit(UiEvent.ShowSnackbar("Tab name required"))
//                    return@launch
//                }
//                if (tabToSave.description.isBlank()) {
//                    _eventFlow.emit(UiEvent.ShowSnackbar("Tab description required"))
//                    return@launch
//                }

//                if(tabToSave.dueDate.isEmpty()){
//                    _eventFlow.emit(UiEvent.ShowSnackbar("Tab due date required"))
//                    return@launch
//                }
//                if(tabToSave.monthlyPayment.isNaN()){
//                    _eventFlow.emit(UiEvent.ShowSnackbar("Tab amount required"))
//                    return@launch
//                }
//
//                repository.addTab(tabToSave)

//                tab = Tab()
                _eventFlow.emit(UiEvent.SubmissionSuccess)

            }
            catch (e: Exception) {
                val errorMessage = "Error saving tab: ${e.message ?: "Unknown error"}"
                _eventFlow.emit(UiEvent.ShowSnackbar(errorMessage))
            }
        }
    }

//    fun onNameChanged(newName: String) {
//        tab = tab.copy(name = newName)
//    }
//    fun onDescriptionChanged(newDescription: String) {
//        tab = tab.copy(description = newDescription)
//    }
//    fun onDueDateChanged(newDueDate: Timestamp) {
//        tab = tab.copy(dueDate = newDueDate)
//    }
//    fun onMonthlyPaymentChanged(newMonthlyPayment: Double) {
//        tab = tab.copy(monthlyPayment = newMonthlyPayment)
//    }

    private val _transactions= MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun observeTabTransactions(tabId: String) {
        viewModelScope.launch {
            repository.getTabTransactions(tabId)
                .collectLatest { transactionList ->
                    _transactions.value = transactionList
                }
        }
    }

    private val _tab=MutableStateFlow<Tab?>(null)
    val singleTab: MutableStateFlow<Tab?> = _tab

    fun fetchTab(tabId: String) {
        viewModelScope.launch {
            repository.getTab(tabId)
                .collectLatest { tab ->
                    _tab.value = tab
                }
        }
    }

    fun fetchTabTransactions(tabID: String) {

    }

    fun getTab(tabID: String) {

    }


    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SubmissionSuccess : UiEvent()
    }
}