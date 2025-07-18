package com.example.cointrail.repository

import android.util.Log
import com.example.cointrail.data.AssetHistory
import com.example.cointrail.data.AssetSearch
import com.example.cointrail.data.Category
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.Stock
import com.example.cointrail.data.Tab
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.User
import com.example.cointrail.data.toAssetHistory
import com.example.cointrail.data.toAssetSearch
import com.example.cointrail.data.toStock
import com.example.cointrail.network.KtorClient
import com.example.cointrail.network.StockAPI
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


internal class RepositoryImpl(private val stockApi: StockAPI)
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
    private val tabsReference=db.collection("tabs")


    // Singleton SharedFlow for all collectors
    override val transactionSharedFlow: SharedFlow<List<Transaction>> =
        currentUser
            .flatMapLatest { user ->
                if (user?.id == null) {
                    flowOf(emptyList()) // don't load anything if user is not available
                } else {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -60)
                    val date = Timestamp(calendar.time)

                    Log.d("Repo", "Firestore query for userID=${user.id}, date >= $date")

                    callbackFlow<List<Transaction>> {
                        val query = transactionsReference
                            .whereGreaterThanOrEqualTo("date", date)
                            .whereEqualTo("userID", user.id)

                        val registration = query.addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.e("Repo", "Firestore error: $error")
                                close(error)
                                return@addSnapshotListener
                            }
                            val transactions = value?.documents?.mapNotNull { doc ->
                                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
                            } ?: emptyList()

                            Log.d("Repo", "Fetched ${transactions.size} transactions from Firestore.")
                            trySend(transactions).isSuccess
                        }

                        awaitClose { registration.remove() }
                    }
                }
            }
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )

    override val categoriesSharedFlow: SharedFlow<List<Category>> =
        currentUser
            .flatMapLatest { user ->
                if (user?.id == null) {
                    flowOf(emptyList()) // don't load anything if user is not available
                } else {
                    callbackFlow<List<Category>> {
                        val query = allCategoriesReference
                            .whereEqualTo("userId", user.id)
                        val registration = query.addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.e("Repo", "Firestore error: $error")
                                close(error)
                                return@addSnapshotListener
                            }
                            val categories = value?.documents?.mapNotNull { doc ->
                                doc.toObject(Category::class.java)?.copy(id = doc.id)
                            } ?: emptyList()
                            Log.d("Categories",categories.toString())
                            Log.d("Repo", "Fetched ${categories.size} categories from Firestore.")
                            trySend(categories).isSuccess
                        }
                        awaitClose { registration.remove() }
                    }
                }
            }
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )

    override val tabsGeneralFlow: SharedFlow<List<Tab>> =
        currentUser
            .flatMapLatest { user ->
                if (user?.id == null) {
                    flowOf(emptyList()) // don't load anything if user is not available
                } else {
                    callbackFlow<List<Tab>> {
                        val query = tabsReference
                            .whereEqualTo("userID", user.id)
                        val registration = query.addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.e("Repo", "Firestore error: $error")
                                close(error)
                                return@addSnapshotListener
                            }
                            val tabs = value?.documents?.mapNotNull { doc ->
                                doc.toObject(Tab::class.java)?.copy(id = doc.id)
                            } ?: emptyList()
                            Log.d("Categories",tabs.toString())
                            Log.d("Repo", "Fetched ${tabs.size} categories from Firestore.")
                            trySend(tabs).isSuccess
                        }
                        awaitClose { registration.remove() }
                    }
                }
            }
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )
    override val savingPocketsGeneralFlow: SharedFlow<List<SavingPocket>> =
        currentUser
            .flatMapLatest { user ->
                if (user?.id == null) {
                    flowOf(emptyList()) // don't load anything if user is not available
                } else {
                    callbackFlow<List<SavingPocket>> {
                        val query = savingPocketsReference
                            .whereEqualTo("userID", user.id)
                        val registration = query.addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.e("Repo", "Firestore error: $error")
                                close(error)
                                return@addSnapshotListener
                            }
                            val savingPockets = value?.documents?.mapNotNull { doc ->
                                doc.toObject(SavingPocket::class.java)?.copy(id = doc.id)
                            } ?: emptyList()
                            Log.d("Categories",savingPockets.toString())
                            Log.d("Repo", "Fetched ${savingPockets.size} categories from Firestore.")
                            trySend(savingPockets).isSuccess
                        }
                        awaitClose { registration.remove() }
                    }
                }
            }
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )

    fun getAllTransactions(): SharedFlow<List<Transaction>> {
        return transactionSharedFlow
    }


    override fun getTransactions(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }



    private val singleTransactionFLow = mutableMapOf<String, SharedFlow<Transaction>>()
    override fun getTransaction(id: String): SharedFlow<Transaction> {
        return singleTransactionFLow.getOrPut(id){
            callbackFlow {
                val docRef = transactionsReference.document(id)
                val registration = docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val transaction = snapshot?.toObject(Transaction::class.java)?.copy(id = snapshot.id)
                    if (transaction != null) {
                        trySend(transaction)
                    }
                }
                awaitClose { registration.remove() }
            }
                .shareIn(
                    scope=repositoryScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    replay = 1
                )
        }
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

    override suspend fun updateTransaction(transaction: Transaction) {
        Log.d("TransactionRepository", "Updating transaction: $transaction")

        val docRef = transactionsReference.document(transaction.id ?: return)

        // Prepare the data map (excluding nulls)
        val updatedData: MutableMap<String, Any> = hashMapOf(
            "amount" to transaction.amount,
            "categoryId" to transaction.categoryId,
            "date" to transaction.date!!, // Ensure non-null if required
            "description" to transaction.description,
           "type" to transaction.type.name,
            "userID" to transaction.userID
        )

        // Update the document
        docRef.update(updatedData).await()
        Log.d("TransactionRepository", "Transaction updated successfully")
    }

    override suspend fun deleteTransaction(transactionID: String): Result<Unit> {
        return try {
            transactionsReference.document(transactionID).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updateBalanceAfterDeletion(documentId: String, transactionAmount: Double) {
        val tabReference = db.collection("tabs").document(documentId)
        val pocketReference = db.collection("savingPockets").document(documentId)

        Log.d(
            "UpdateBalance",
            "Starting updateBalanceAfterDeletion for documentId: $documentId, transactionAmount: $transactionAmount"
        )

        // Try tabs first
        val tabSnapshot = tabReference.get().await()
        Log.d(
            "UpdateBalance",
            "Checked tabs collection for $documentId. Exists: ${tabSnapshot.exists()}"
        )
        if (tabSnapshot.exists()) {
            val currentBalance = tabSnapshot.getDouble("outstandingBalance") ?: 0.0
            val newBalance = maxOf(currentBalance - transactionAmount, 0.0)
            Log.d(
                "UpdateBalance",
                "Tab found. Current outstandingBalance: $currentBalance, New outstandingBalance (clamped): $newBalance"
            )
            tabReference.update("outstandingBalance", newBalance).await()
            Log.d(
                "UpdateBalance",
                "Updated outstandingBalance in tabs for $documentId to $newBalance"
            )
            return
        }

        // If not found in tabs, try savingPockets
        val pocketSnapshot = pocketReference.get().await()
        Log.d(
            "UpdateBalance",
            "Checked savingPockets collection for $documentId. Exists: ${pocketSnapshot.exists()}"
        )
        if (pocketSnapshot.exists()) {
            val currentBalance = pocketSnapshot.getDouble("balance") ?: 0.0
            val newBalance = maxOf(currentBalance - transactionAmount, 0.0)
            Log.d(
                "UpdateBalance",
                "SavingPocket found. Current balance: $currentBalance, New balance (clamped): $newBalance"
            )
            pocketReference.update("balance", newBalance).await()
            Log.d(
                "UpdateBalance",
                "Updated balance in savingPockets for $documentId to $newBalance"
            )
            return
        }

        // If neither exists
        Log.w(
            "UpdateBalance",
            "Document $documentId not found in either tabs or savingPockets. No update performed."
        )
    }


    override suspend fun updateBalanceAfterTransactionEdit(
        documentId: String,
        oldAmount: Double,
        newAmount: Double
    ) {
        val tabReference = tabsReference.document(documentId)
        val pocketReference = savingPocketsReference.document(documentId)

        Log.d(
            "UpdateBalance",
            "Starting updateBalanceAfterTransactionEdit for documentId: $documentId, oldAmount: $oldAmount, newAmount: $newAmount"
        )

        // Calculate the difference (positive or negative)
        val difference = newAmount - oldAmount

        // Try tabs first
        val tabSnapshot = tabReference.get().await()
        Log.d(
            "UpdateBalance",
            "Checked tabs collection for $documentId. Exists: ${tabSnapshot.exists()}"
        )
        if (tabSnapshot.exists()) {
            val currentBalance = tabSnapshot.getDouble("outstandingBalance") ?: 0.0
            val newBalance = maxOf(currentBalance + difference, 0.0)
            Log.d(
                "UpdateBalance",
                "Tab found. Current outstandingBalance: $currentBalance, New outstandingBalance (clamped): $newBalance"
            )
            tabReference.update("outstandingBalance", newBalance).await()
            Log.d(
                "UpdateBalance",
                "Updated outstandingBalance in tabs for $documentId to $newBalance"
            )
            return
        }

        // If not found in tabs, try savingPockets
        val pocketSnapshot = pocketReference.get().await()
        Log.d(
            "UpdateBalance",
            "Checked savingPockets collection for $documentId. Exists: ${pocketSnapshot.exists()}"
        )
        if (pocketSnapshot.exists()) {
            val currentBalance = pocketSnapshot.getDouble("balance") ?: 0.0
            val newBalance = maxOf(currentBalance + difference, 0.0)
            Log.d(
                "UpdateBalance",
                "SavingPocket found. Current balance: $currentBalance, New balance (clamped): $newBalance"
            )
            pocketReference.update("balance", newBalance).await()
            Log.d(
                "UpdateBalance",
                "Updated balance in savingPockets for $documentId to $newBalance"
            )
            return
        }

        // If neither exists
        Log.w(
            "UpdateBalance",
            "Document $documentId not found in either tabs or savingPockets. No update performed."
        )
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

    override suspend fun addTabTransaction(transaction: Transaction) {
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
        }
        catch (e: Exception){
            throw e
        }
    }

    override suspend fun updateTabBalance(tabID: String, newBalance: Double) {
        try {
            tabsReference.document(tabID)
                .update("outstandingBalance", newBalance)
        } catch (e: Exception) {
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



    override fun signOut() {
        auth.signOut()
    }

    val tabsSharedFlow: SharedFlow<List<Tab>> by lazy {
        callbackFlow <List<Tab>> {
            val query = tabsReference
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

    private val tabsFlows = mutableMapOf<String, SharedFlow<Tab>>()
    override fun getTab(tabId: String): SharedFlow<Tab> {
        return tabsFlows.getOrPut(tabId) {
            callbackFlow {
                val registration=tabsReference.document(tabId).addSnapshotListener{ snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val tab=snapshot?.toObject<Tab>()?.copy(id=snapshot.id)
                    if (tab != null) {
                        trySend(tab)
                    }
                }
                awaitClose { registration.remove() }
            }.shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )
        }
    }

    override suspend fun addTab(tab: Tab) {
        try{
            val data= hashMapOf(
                "name" to tab.name,
                "description" to tab.description,
                "userID" to tab.userID,
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

    override fun searchAssets(query: String): Flow<List<AssetSearch>> {
        return flow {
            val response = stockApi.searchAsset(query)
            val body = response.body ?: emptyList()
            emit(body.map { it.toAssetSearch() })
        }.catch { e ->
            // Optionally log the error for debugging
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }
    override fun fetchAssetDetails(symbol: String, type: String): Flow<Stock> {
        return flow {
            val response = stockApi.getAssetDetails(symbol, type)
            Log.d("Response", response.toString())
            val stock = response.body?.toStock() ?: Stock()
            Log.d("Stock", stock.toString())
            emit(stock)
            Log.d("StockSent",stock.toString())
        }.catch { e ->
            Log.d("ErrorLog", e.toString())
            // Optionally log the error
            emit(Stock())
        }.flowOn(Dispatchers.IO)
    }

    override fun fetchAssetHistory(symbol: String): Flow<List<AssetHistory>> {
        return flow<List<AssetHistory>> {
            val response=stockApi.fetchAssetHistory(symbol)
            Log.d("Response", response.toString())
            val body=response.body
            Log.d("History", body.toString())
            emit(body.map { it.toAssetHistory() })
            Log.d("History", body.toString())
        }.catch { e ->
            Log.d("ErrorLog", e.toString())
            // Optionally log the error
            emit(emptyList())
        }
    }
}