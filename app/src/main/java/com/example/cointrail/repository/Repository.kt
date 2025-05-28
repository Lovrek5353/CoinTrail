package com.example.cointrail.repository

import com.example.cointrail.data.Category
import com.example.cointrail.data.Tab
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface Repository {

    val currentUser: StateFlow<User?>

    fun getAllTransactions(): SharedFlow<List<Transaction>>
    fun getTransactions(): SharedFlow<List<Transaction>>
    fun getTransaction(): SharedFlow<Transaction>

    fun getCategoryTransactions(categoryId: String): SharedFlow<List<Transaction>>

    fun addTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun deleteTransaction(transaction: Transaction)

    fun getCategories(): SharedFlow<List<Category>>
    fun getCategory(categoryId: String): SharedFlow<Category>
    suspend fun addCategory(category: Category)
    fun updateCategory(category: Category)
    fun deleteCategory(category: Category)

    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>>
    fun emailSignUp(email: String, password: String): Flow<Result<AuthResult>>
    fun signOut()

    fun getTabs(): SharedFlow<List<Tab>>
    fun getTab(tabId: String): SharedFlow<Tab>
    suspend fun addTab(tab: Tab)
    fun updateTab(tab: Tab)
    fun deleteTab(tab: Tab)


    suspend fun fetchUserByEmail(email: String)



}