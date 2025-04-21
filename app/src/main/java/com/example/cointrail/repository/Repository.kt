package com.example.cointrail.repository

import com.example.cointrail.data.Category
import com.example.cointrail.data.Transaction
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface Repository {
    fun getAllTransactions(): SharedFlow<List<Transaction>>
    fun getTransactions(): SharedFlow<List<Transaction>>
    fun getTransaction(): SharedFlow<Transaction>

    fun addTransaction(transaction: Transaction)
    fun updateTransaction(transaction: Transaction)
    fun deleteTransaction(transaction: Transaction)

    fun getCategories(): SharedFlow<List<Category>>
    fun addCategory(category: Category)
    fun updateCategory(category: Category)
    fun deleteCategory(category: Category)

    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>>
    fun emailSignUp(email: String, password: String): Flow<Result<AuthResult>>
    fun signOut()


}