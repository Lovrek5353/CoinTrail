package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cointrail.data.Category
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Tab
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    private val _categorySums = MutableStateFlow<Map<String, Double>>(emptyMap())
    val categorySums: StateFlow<Map<String, Double>> = _categorySums

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories=MutableStateFlow<List<Category>>(emptyList())
    private val categories: StateFlow<List<Category>> = _categories

    private val _tabs=MutableStateFlow<List<Tab>>(emptyList())
    private val tabs: StateFlow<List<Tab>> = _tabs

    private val _savingPockets=MutableStateFlow<List<SavingPocket>>(emptyList())
    private val savingPockets: StateFlow<List<SavingPocket>> = _savingPockets

    private val _resolvedCategorySums = MutableStateFlow<Map<String, Double>>(emptyMap())
    val resolvedCategorySums: StateFlow<Map<String, Double>> = _resolvedCategorySums

    private val _resolvedTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val resolvedTransactions: StateFlow<List<Transaction>> = _resolvedTransactions




    init {
        // Transaction + category sums
        viewModelScope.launch {
            repository.transactionSharedFlow
                .map { txList ->
                    _transactions.value = txList
                    txList.groupBy { it.categoryId }
                        .mapValues { it.value.sumOf { tx -> tx.amount } }
                }
                .collect { mapped ->
                    _categorySums.value = mapped
                }
        }


        // Load categories
        viewModelScope.launch {
            repository.categoriesSharedFlow.collect {
                _categories.value = it
                Log.d("Categories", "Updated with: $it")
            }
        }

        // Load tabs
        viewModelScope.launch {
            repository.tabsGeneralFlow.collect {
                _tabs.value = it
                Log.d("Tabs", "Updated with: $it")
            }
        }

        // Load saving pockets
        viewModelScope.launch {
            repository.savingPocketsGeneralFlow.collect {
                _savingPockets.value = it
                Log.d("SavingPockets", "Updated with: $it")
            }
        }

        // Combine everything to generate resolvedCategorySums
        viewModelScope.launch {
            combine(
                categorySums,
                categories,
                tabs,
                savingPockets
            ) { sumMap, categoryList, tabList, pocketList ->

                // Early exit if lists are not fully loaded
                if (categoryList.isEmpty() && tabList.isEmpty() && pocketList.isEmpty())
                    return@combine emptyMap()

                val lookup = buildMap<String, String> {
                    categoryList.forEach { it.id?.let { id -> put(id, it.name) } }
                    tabList.forEach { it.id?.let { id -> put(id, it.name) } }
                    pocketList.forEach { it.id?.let { id -> put(id, it.name) } }
                }

                return@combine sumMap.mapKeys { entry ->
                    lookup[entry.key] ?: entry.key
                }

            }.collect { resolvedMap ->
                _resolvedCategorySums.value = resolvedMap
                Log.d("ResolvedSums", "Updated with: $resolvedMap")
            }
        }
        viewModelScope.launch {
            combine(
                transactions,
                categories,
                tabs,
                savingPockets
            ) { txList, categoryList, tabList, pocketList ->

                // Create ID -> name lookup
                val nameLookup = buildMap<String, String> {
                    categoryList.forEach { it.id?.let { id -> put(id, it.name) } }
                    tabList.forEach { it.id?.let { id -> put(id, it.name) } }
                    pocketList.forEach { it.id?.let { id -> put(id, it.name) } }
                }

                // Create updated list of transactions with category name
                txList.map { tx ->
                    tx.copy(
                        categoryId = nameLookup[tx.categoryId] ?: tx.categoryId
                    )
                }

            }.collect { resolvedList ->
                _resolvedTransactions.value = resolvedList
                Log.d("ResolvedTransactions", "Mapped transactions = $resolvedList")
            }
        }

    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SubmissionSuccess : UiEvent()
    }
}