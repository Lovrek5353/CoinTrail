package com.example.cointrail.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Category
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.repository.Repository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel(
    var repository: Repository
): ViewModel(){
    val user: StateFlow<User?> = repository.currentUser
    var transaction by mutableStateOf(Transaction())
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var amountInputString by mutableStateOf("")
        private set

    var descriptionInputString by mutableStateOf("")
        private set
    

    fun onAmountInputChange(newAmountString: String) {
        amountInputString = newAmountString
        // Optional: check user input and update as it enters the data
    }

    fun onDateSelected(newDate: Long?) {
        transaction = transaction.copy(
            date = newDate?.let { Timestamp(Date(it)) }
        )
    }

    fun onCategorySelected(category: Category) {
        if (category.id != null) {
            transaction = transaction.copy(
                categoryId = category.id
            )
        }
    }

    fun onDescriptionChange(newDescription: String) {
        descriptionInputString = newDescription
    }

    fun transactionTypeSelected(type: TransactionType) {
        transaction = transaction.copy(type = type)
    }



    fun onSubmit(){
        viewModelScope.launch {
            try{
                val currentUser = user.value
                val userId = currentUser?.id ?: run{
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    return@launch
                }

                //Validate fields
                if(amountInputString.isBlank()){
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount is required"));
                    return@launch
                }

                val amountValue=amountInputString.toDoubleOrNull()
                if(amountValue==null){
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"));
                    return@launch
                }
                if(amountValue<=0){
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount must be greater than zero"));
                    return@launch
                }
                if(descriptionInputString.isBlank()){
                    _eventFlow.emit(UiEvent.ShowSnackbar("Description is required"));
                    return@launch
                }

                val transactionToSave = transaction.copy(
                    userID = userId,
                    amount = amountValue,
                    description = descriptionInputString
                )

                repository.addTransaction(transactionToSave)
                _eventFlow.emit(UiEvent.SubmissionSuccess)

                //Reset fields
                transaction = Transaction()
                amountInputString = ""
            }
            catch (e: Exception){
                val errorMessage = "Error saving transaction: ${e.message ?: "Unknown error"}"
                _eventFlow.emit(UiEvent.ShowSnackbar(errorMessage))
            }
        }
    }

    fun fetchCategories(): SharedFlow<List<Category>> {
        return repository.getCategories()
    }




    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SubmissionSuccess : UiEvent()
    }
}