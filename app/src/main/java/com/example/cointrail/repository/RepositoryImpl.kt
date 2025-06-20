package com.example.cointrail.repository

import android.util.Log
import com.example.cointrail.data.Category
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Tab
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

internal class RepositoryImpl
    : Repository
{
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    override fun getAllTransactionsByUser(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    private val flowScope = CoroutineScope(Dispatchers.Default)
    private val repositoryScope= CoroutineScope(Dispatchers.IO+ SupervisorJob())

    private val db= Firebase.firestore
    private val auth= FirebaseAuth.getInstance()

    private val allCategoriesReference=db.collection("categories")
    private val usersReference=db.collection("users")
    private val transactionsReference=db.collection("transactions")
    private val savingPocketsReference=db.collection("savingPockets")


    // Singleton SharedFlow for all collectors
    val transactionSharedFlow: SharedFlow<List<Transaction>> by lazy {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -60)
        val date = Timestamp(calendar.time)

        callbackFlow<List<Transaction>> {
            val query = transactionsReference
                .whereGreaterThanOrEqualTo("date", date)
                .whereEqualTo("userID", currentUser.value?.id ?: "")

            val registration = query.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val transactions = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)
                } ?: emptyList()
                trySend(transactions).isSuccess
            }

            awaitClose { registration.remove() }
        }.shareIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            replay = 1
        )
    }


    fun getAllTransactions(): SharedFlow<List<Transaction>> {
        return transactionSharedFlow
    }


    override fun getTransactions(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransaction(): SharedFlow<Transaction> {
        TODO("Not yet implemented")
    }



    // Cache flows by categoryId to share Firestore listeners
    private val transactionFlows = mutableMapOf<String, SharedFlow<List<Transaction>>>()

    override fun getCategoryTransactions(categoryId: String): SharedFlow<List<Transaction>> {
        return transactionFlows.getOrPut(categoryId) {
            callbackFlow {
                val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -60) }
                val date = Timestamp(calendar.time)
                val query = db.collection("transactions")
                    .whereEqualTo("categoryId", categoryId)
                    .whereGreaterThanOrEqualTo("date", date)
                val registration = query.addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("TransactionRepository", "Firebase listener error: $error")
                        close(error)
                        return@addSnapshotListener
                    }
                    val transactions = value?.documents?.mapNotNull { doc ->
                        doc.toObject(Transaction::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    Log.d("TransactionRepository", "Firebase emitted ${transactions.size} transactions.")
                    val result = trySend(transactions)
                    if (result.isFailure) {
                        Log.e("TransactionRepository", "Failed to send transactions to flow: ${result.exceptionOrNull()}")
                    }
                }
                awaitClose {
                    Log.d("TransactionRepository", "Firebase listener for category $categoryId removed.")
                    registration.remove()
                }
            }.shareIn(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()), // Use the class-level scope
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )
        }
    }



    override fun getTabTransactions(tabId: String): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override suspend fun addTransaction(transaction: Transaction) {
        try{
            val data= hashMapOf(
                "userID" to transaction.userID,
                "amount" to transaction.amount,
                "date" to transaction.date,
                "categoryId" to transaction.categoryId,
                "description" to transaction.description,
                "type" to transaction.type
            )
            transactionsReference.add(data).await()
        } catch (e: Exception){
            throw e
        }
    }

    override fun updateTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override fun deleteTransaction(transaction: Transaction) {

        TODO("Not yet implemented")
    }

    override suspend fun addSavingPocketTransaction(transaction: Transaction) {
        try{
            val data= hashMapOf(
                "userID" to transaction.userID,
                "amount" to transaction.amount,
                "date" to transaction.date,
                "categoryId" to transaction.categoryId,
                "description" to transaction.description,
            )
            transactionsReference.add(data).await()
        } catch (e: Exception){
            throw e
        }
    }

    override suspend fun updateSavingPocketBalance(savingPocketID: String, newBalance: Double) {
        try {
            savingPocketsReference.document(savingPocketID)
                .update("balance", newBalance)
        } catch (e: Exception){
            throw e
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val categoriesFlow = currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(emptyList())
        } else {
            createUserCategoriesFlow(user.id!!)
        }
    }.shareIn(
        flowScope,
        SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    private fun createUserCategoriesFlow(userId: String): Flow<List<Category>> = callbackFlow {
        val query = allCategoriesReference
            .whereEqualTo("userId", userId)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val categories = snapshot?.documents?.mapNotNull {
                it.toObject<Category>()?.copy(id = it.id)  // Preserve Firestore document ID
            } ?: emptyList()

            trySend(categories)
        }

        awaitClose { registration.remove() }
    }

    override fun getCategories(): SharedFlow<List<Category>> = categoriesFlow


    private val categoryFlows = mutableMapOf<String, SharedFlow<Category>>()

    override fun getCategory(categoryId: String): SharedFlow<Category> {
        return categoryFlows.getOrPut(categoryId) {
            callbackFlow {
                val docRef = db.collection("categories").document(categoryId)
                val registration = docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val category = snapshot?.toObject(Category::class.java)?.copy(id = snapshot.id)
                    if (category != null) {
                        trySend(category)
                    }
                }
                awaitClose { registration.remove() }
            }.shareIn(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )
        }
    }


    override suspend fun addCategory(category: Category) {
        try {
            val data = hashMapOf(
                "name" to category.name,
                "description" to category.description,
                "userId" to category.userId
            )
            allCategoriesReference.add(data).await()
        } catch (e: Exception) {
            throw e
        }
    }


    override fun updateCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override fun emailLogin(email: String, password: String): Flow<Result<AuthResult>> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Result.success(task.result)).isSuccess
                    // Launch a coroutine to fetch user info
                    // Use applicationScope or a custom CoroutineScope
                    // (Don't use MainScope or Dispatchers.Main in a repository)
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        try {
                            fetchUserByEmail(email)
                        } catch (e: Exception) {
                            // Optionally handle or log error
                        }
                    }
                    // Optionally, you could await job completion if needed
                } else {
                    trySend(Result.failure(task.exception ?: Exception("Unknown error"))).isSuccess
                }
                close()
            }
        awaitClose()
    }

    override suspend fun emailSignUp(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        Log.d("RepositoryImpl", "Sending password reset email to: $email")
        return try {
            Log.d("RepositoryImpl", "Password reset email sent successfully")
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RepositoryImpl", "Error sending password reset email", e)
            Result.failure(e)
        }
    }


//    override fun emailSignUp(email: String, password: String): Flow<Result<AuthResult>> {
//        TODO("Not yet implemented")
//    }

    override fun signOut() {
        TODO("Not yet implemented")
    }

    val tabsSharedFlow: SharedFlow<List<Tab>> by lazy {
        callbackFlow <List<Tab>> {
            val query = db.collection("tabs")
                .whereEqualTo("userID", currentUser.value?.id)
            val registration = query.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tabs = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Tab::class.java)
                } ?: emptyList()
                trySend(tabs).isSuccess
            }
            awaitClose { registration.remove() }
        }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
    }

    override fun getTabs(): SharedFlow<List<Tab>> {
        return tabsSharedFlow
    }


    override fun getTab(tabId: String): SharedFlow<Tab> {
        TODO("Not yet implemented")
    }

    override suspend fun addTab(tab: Tab) {
        try{
            val data= hashMapOf(
                "name" to tab.name,
                "description" to tab.description,
                "userID" to tab.userId,
                "initialAmount" to tab.initialAmount,
                "outstandingBalance" to tab.outstandingBalance,
                "startDate" to tab.startDate,
                "dueDate" to tab.dueDate,
                "monthlyPayment" to tab.monthlyPayment,
                "lender" to tab.lender,
                "status" to tab.status
            )
            db.collection("tabs").add(data).await()
        }
        catch (e: Exception){
            throw e
        }
    }

    override fun updateTab(tab: Tab) {
        TODO("Not yet implemented")
    }

    override fun deleteTab(tab: Tab) {
        TODO("Not yet implemented")
    }

    val savingPocketsSharedFlow: SharedFlow<List<SavingPocket>> by lazy {
        callbackFlow<List<SavingPocket>> {
            val query = db.collection("savingPockets")
                .whereEqualTo("userID", currentUser.value?.id)

            val registration=query.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val savingPockets = value?.documents?.mapNotNull { doc ->
                    doc.toObject(SavingPocket::class.java)
                } ?: emptyList()
                trySend(savingPockets).isSuccess
            }
            awaitClose { registration.remove() }
            }.shareIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1)
        }

    override fun getSavingPockets(): SharedFlow<List<SavingPocket>> {
        return savingPocketsSharedFlow
    }

    private val savingPocketFlows= mutableMapOf<String, SharedFlow<SavingPocket>>()

    override fun getSavingPocket(pocketID: String): SharedFlow<SavingPocket> {
        return savingPocketFlows.getOrPut(pocketID) {
            callbackFlow {
                val docRef = savingPocketsReference.document(pocketID)
                val registration = docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val savingPocket = snapshot?.toObject(SavingPocket::class.java)?.copy(id = snapshot.id)
                    if (savingPocket != null) {
                        trySend(savingPocket)
                    }
                }
                awaitClose { registration.remove() }
            }.shareIn(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1)
        }
    }

    override suspend fun addSavingPocket(savingPocket: SavingPocket) {
        Log.d("RepositoryImpl", "Adding saving pocket: $savingPocket")
        try{
            val data= hashMapOf(
                "name" to savingPocket.name,
                "description" to savingPocket.description,
                "userID" to savingPocket.userID,
                "targetAmount" to savingPocket.targetAmount,
                "targetDate" to savingPocket.targetDate,
                "balance" to 0.0
            )
            savingPocketsReference.add(data).await()
        }
        catch (e: Exception){
            throw e
        }
    }

    override fun updateSavingPocket(savingPocket: SavingPocket) {
        TODO("Not yet implemented")
    }

    override fun deleteSavingPocket(savingPocket: SavingPocket) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchUserByEmail(email: String) {
        try {
            val querySnapshot = usersReference
                .whereEqualTo("email", email)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                throw NoSuchElementException("User with email $email not found")
            }

            val user = querySnapshot.documents.first().toObject<User>()
                ?: throw NullPointerException("User data corrupted")

            _currentUser.value = user

        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user", e)
            throw e
        }
    }
}