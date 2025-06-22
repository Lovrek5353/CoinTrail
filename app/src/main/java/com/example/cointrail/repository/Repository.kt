package com.example.cointrail.repository

import com.example.cointrail.data.Category
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Tab
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface Repository {

    val currentUser: StateFlow<User?>

    fun getAllTransactionsByUser(): SharedFlow<List<Transaction>>
    fun getTransactions(): SharedFlow<List<Transaction>>
    fun getTransaction(): SharedFlow<Transaction>

    fun getCategoryTransactions(categoryId: String): SharedFlow<List<Transaction>>
    fun getTabTransactions(tabId: String): SharedFlow<List<Transaction>>

    suspend fun addTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun deleteTransaction(transaction: Transaction)

    suspend fun addSavingPocketTransaction(transaction: Transaction)
    suspend fun updateSavingPocketBalance(savingPocketID: String, newBalance: Double)

    suspend fun addTabTransaction(transaction: Transaction)
    suspend fun updateTabBalance(tabID: String, newBalance: Double)

    fun getCategories(): SharedFlow<List<Category>>
    fun getCategory(categoryId: String): SharedFlow<Category>
    suspend fun addCategory(category: Category)
    fun updateCategory(category: Category)
    fun deleteCategory(category: Category)

    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>>
    //fun emailSignUp(email: String, password: String): Flow<Result<AuthResult>>
    suspend fun emailSignUp(email: String, password: String): Result<Unit>
    fun signOut()

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    fun getTabs(): SharedFlow<List<Tab>>
    fun getTab(tabId: String): SharedFlow<Tab>
    suspend fun addTab(tab: Tab)
    fun updateTab(tab: Tab)
    fun deleteTab(tab: Tab)

    fun getSavingPockets(): SharedFlow<List<SavingPocket>>
    fun getSavingPocket(pocketID: String): SharedFlow<SavingPocket>
    suspend fun addSavingPocket(savingPocket: SavingPocket)
    fun updateSavingPocket(savingPocket: SavingPocket)
    fun deleteSavingPocket(savingPocket: SavingPocket)


    suspend fun fetchUserByEmail(email: String)



}