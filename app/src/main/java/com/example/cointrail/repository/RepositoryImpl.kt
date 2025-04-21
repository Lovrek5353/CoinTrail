package com.example.cointrail.repository

import com.example.cointrail.data.Category
import com.example.cointrail.data.Transaction
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

internal class RepositoryImpl
    (
            //add necessary components
            ) : Repository
{
    private val flowScope = CoroutineScope(Dispatchers.Default)

    private val db= Firebase.firestore



    override fun getAllTransactions(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransactions(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransaction(): SharedFlow<Transaction> {
        TODO("Not yet implemented")
    }

    override fun addTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun updateTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun deleteTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun getCategories(): SharedFlow<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun addCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override fun updateCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override fun emailLogin(email: String, password: String): Flow<Result<AuthResult>> {
        TODO("Not yet implemented")
    }

    override fun emailSignUp(email: String, password: String): Flow<Result<AuthResult>> {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }

}