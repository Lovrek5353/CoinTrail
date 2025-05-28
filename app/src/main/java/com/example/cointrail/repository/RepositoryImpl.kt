package com.example.cointrail.repository

import android.util.Log
import com.example.cointrail.data.Category
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
    private val flowScope = CoroutineScope(Dispatchers.Default)

    private val db= Firebase.firestore
    private val auth= FirebaseAuth.getInstance()

    private val allCategoriesReference=db.collection("categories")
    private val usersReference=db.collection("users")




    override fun getAllTransactions(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransactions(): SharedFlow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransaction(): SharedFlow<Transaction> {
        TODO("Not yet implemented")
    }

    override fun getCategoryTransactions(categoryId: String): SharedFlow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -60)
        val date = Timestamp(calendar.time)

        val flow = callbackFlow<List<Transaction>> {
            val query = db.collection("transacitons")
                .whereEqualTo("categoryId", categoryId)
                .whereGreaterThanOrEqualTo("date", date)

            val registration = query.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val transactions = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)
                } ?: emptyList()
                trySend(transactions)
            }
            awaitClose { registration.remove() }
        }

        // Share the flow so multiple collectors get the same data
        return flow.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val categoriesFlow = currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(emptyList<Category>())
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
    override fun getCategory(categoryId: String): SharedFlow<Category> {
        val flow = callbackFlow<Category> {
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
        }
        return flow.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
    }




    // In RepositoryImpl
    override suspend fun addCategory(category: Category) {
        try {
            val data = hashMapOf(
                "name" to category.name,
                "description" to category.description,
                "userId" to category.userId // Add this line
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


    override fun emailSignUp(email: String, password: String): Flow<Result<AuthResult>> {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }

    override fun getTabs(): SharedFlow<List<Tab>> {
        TODO("Not yet implemented")
    }

    override fun getTab(tabId: String): SharedFlow<Tab> {
        TODO("Not yet implemented")
    }

    override suspend fun addTab(tab: Tab) {
        TODO("Not yet implemented")
    }

    override fun updateTab(tab: Tab) {
        TODO("Not yet implemented")
    }

    override fun deleteTab(tab: Tab) {
        TODO("Not yet implemented")
    }

    // In your UserRepository implementation
    override suspend fun fetchUserByEmail(email: String) {
        try {
            // 1. Query Firestore for user by email
            val querySnapshot = usersReference
                .whereEqualTo("email", email)
                .get()
                .await() // Wait for the single query result

            // 2. Handle results
            if (querySnapshot.isEmpty) {
                throw NoSuchElementException("User with email $email not found")
            }

            // 3. Convert first matching document to User object
            val user = querySnapshot.documents.first().toObject<User>()
                ?: throw NullPointerException("User data corrupted")

            // 4. Update repository state
            _currentUser.value = user

        } catch (e: Exception) {
            // 5. Handle errors and rethrow for ViewModel
            Log.e("UserRepository", "Error fetching user", e)
            throw e
        }
    }


}