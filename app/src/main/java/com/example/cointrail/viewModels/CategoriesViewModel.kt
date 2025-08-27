import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.Category
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import com.google.firebase.Timestamp

class CategoriesViewModel(
    private val repository: Repository
) : ViewModel() {

    // Observe the current user reactively
    val user: StateFlow<User?> = repository.currentUser

    // Form state
    var category by mutableStateOf(Category())
        private set

    // UI event flow for Snackbar/navigation
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onSubmit() {
        viewModelScope.launch {
            try {
                // Always get the latest user value
                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    return@launch
                }

                // Create a copy of the category with the userId
                val categoryToSave = category.copy(userId = userId)

                // Validate fields
                if (categoryToSave.name.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Category name required"))
                    return@launch
                }
                if (categoryToSave.description.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Category description required"))
                    return@launch
                }

                // Save to repository
                repository.addCategory(categoryToSave)

                // Reset the form and notify UI
                category = Category()
                _eventFlow.emit(UiEvent.SubmissionSuccess)

            } catch (e: Exception) {
                val errorMessage = "Error saving category: ${e.message ?: "Unknown error"}"
                _eventFlow.emit(UiEvent.ShowSnackbar(errorMessage))
            }
        }
    }

    fun onNameChanged(newName: String) {
        category = category.copy(name = newName)
    }

    fun onDescriptionChanged(newDescription: String) {
        category = category.copy(description = newDescription)
    }

    fun fetchCategories(): SharedFlow<List<Category>> {
        return repository.getCategories()
    }

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun observeTransactions(categoryId: String) {
        viewModelScope.launch {
            repository.getCategoryTransactions(categoryId)
                .collect { txList ->
                    _transactions.value = txList
                }
        }
    }

    private val _category = MutableStateFlow<Category?>(null)
    val singleCategory: MutableStateFlow<Category?> = _category

    fun fetchCategory(categoryId: String) {
        viewModelScope.launch {
            repository.getCategory(categoryId)
                .collectLatest { category ->
                    _category.value = category
                }
        }
    }

    var transactionAmountString by mutableStateOf("")
        private set
    var transactionDescriptionString by mutableStateOf("")
        private set
    var transactionDateMillis by mutableStateOf<Long?>(null)
        private set
    var transactionCategoryID by mutableStateOf("")
        private set

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

    fun onTransactionSubmit(){
        Log.d("CategoriesViewModel", "onTransactionSubmit called")
        viewModelScope.launch {
            try{
                val currentUser = user.value
                val userId = currentUser?.id ?: run {
                    _eventFlow.emit(UiEvent.ShowSnackbar("User not logged in"))
                    Log.d("CategoriesViewModel", "User not logged in")
                    return@launch
                }

                //Validation
                if (transactionAmountString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Amount cannot be empty"))
                    Log.d("CategoriesViewModel", "Amount cannot be empty")
                    return@launch
                }
                val amountValue = transactionAmountString.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Invalid amount"))
                    Log.d("CategoriesViewModel", "Invalid amount")
                    return@launch
                }
                if (transactionDescriptionString.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Description cannot be empty"))
                    Log.d("CategoriesViewModel", "Description cannot be empty")
                    return@launch
                }
                if (transactionDateMillis == null) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Date cannot be empty"))
                    Log.d("CategoriesViewModel", "Date cannot be empty")
                    return@launch
                }
                if (transactionCategoryID.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Category cannot be empty"))
                    Log.d("CategoriesViewModel", "Category cannot be empty")
                    return@launch
                }

                //Create transaction object

                val validatedTransaction = Transaction(
                    amount = amountValue,
                    description = transactionDescriptionString,
                    date = Timestamp(Date(transactionDateMillis!!)),
                    categoryId = transactionCategoryID,
                    userID = userId
                )

                //Add transaction
                repository.addTransaction(validatedTransaction)
                Log.d("CategoriesViewModel", "Transaction added successfully")

                _eventFlow.emit(CategoriesViewModel.UiEvent.SubmissionSuccess)
                //Reset transaction form fields
                transactionAmountString = ""
                transactionDescriptionString = ""
                transactionDateMillis = null
                transactionCategoryID = ""
            }
            catch (e: Exception) {
                val errorMessage = "Error saving transaction: ${e.message ?: "Unknown error"}"
                _eventFlow.emit(UiEvent.ShowSnackbar(errorMessage))
            }
        }
    }


    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SubmissionSuccess : UiEvent()
    }
}
