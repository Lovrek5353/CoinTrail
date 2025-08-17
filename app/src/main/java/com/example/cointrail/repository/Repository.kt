package com.example.cointrail.repository

import com.example.cointrail.data.AssetHistory
import com.example.cointrail.data.AssetSearch
import com.example.cointrail.data.Category
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Stock
import com.example.cointrail.data.Tab
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface Repository {

    val currentUser: StateFlow<User?>
    val transactionSharedFlow: SharedFlow<List<Transaction>>
    val categoriesSharedFlow: SharedFlow<List<Category>>
    val tabsGeneralFlow: SharedFlow<List<Tab>>
    val savingPocketsGeneralFlow: SharedFlow<List<SavingPocket>>
    val stocksSharedFlow: SharedFlow<List<Stock>>

    fun getAllTransactionsByUser(): SharedFlow<List<Transaction>>
    fun getTransactions(): SharedFlow<List<Transaction>>
    fun getTransaction(id: String): SharedFlow<Transaction>

    fun getCategoryTransactions(categoryId: String): SharedFlow<List<Transaction>>
    fun getTabTransactions(tabId: String): SharedFlow<List<Transaction>>

    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionID: String): Result<Unit>

    suspend fun updateBalanceAfterDeletion(documentId: String, newBalance: Double)
    suspend fun updateBalanceAfterTransactionEdit(
        documentId: String,
        oldAmount: Double,
        newAmount: Double
    )

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

    fun searchAssets(query: String): Flow<List<AssetSearch>>
    fun fetchAssetDetails(symbol: String, type: String="STOCKS"): Flow<Stock>
    fun fetchAssetHistory(symbol: String): Flow<List<AssetHistory>>
    suspend fun addStockToDB(stock: Stock)
    fun getStocks(): Flow<List<Stock>>
    fun getStock(stockID: String): Flow<Stock>

    suspend fun updateStockInfo(stockID: String, value: Double)

    suspend fun removeFromFavorite(Stock: AssetSearch)
    suspend fun addToFavorite(Stock: AssetSearch)
    fun getFavorites(): Flow<List<AssetSearch>>



    fun deleteData(userID: String?)

    suspend fun signInWithGoogle(idToken: String): Result<User>

}