package com.example.cointrail.viewModels

import AnalyticsViewModel
import com.example.cointrail.MainDispatcherRule
import com.example.cointrail.data.*
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.repository.Repository
import com.google.firebase.Timestamp
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    // Use your MainDispatcherRule to set main dispatcher for coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: Repository
    private lateinit var viewModel: AnalyticsViewModel

    // MutableFlows to emit test data
    private val currentUserFlow = MutableStateFlow<User?>(null)
    private val transactionSharedFlow = MutableSharedFlow<List<Transaction>>()
    private val categoriesSharedFlow = MutableSharedFlow<List<Category>>()
    private val savingPocketsSharedFlow = MutableSharedFlow<List<SavingPocket>>()
    private val tabsSharedFlow = MutableSharedFlow<List<Tab>>()

    // Mock the static Log class to prevent crashes
    private val mockLog = mockStatic(android.util.Log::class.java)

    @Before
    fun setup() {
        repository = mock()

        // Stub repository flows
        whenever(repository.currentUser).thenReturn(currentUserFlow)
        whenever(repository.transactionSharedFlow).thenReturn(transactionSharedFlow)
        whenever(repository.categoriesSharedFlow).thenReturn(categoriesSharedFlow)
        whenever(repository.savingPocketsGeneralFlow).thenReturn(savingPocketsSharedFlow)
        whenever(repository.tabsGeneralFlow).thenReturn(tabsSharedFlow)

        viewModel = AnalyticsViewModel(repository)


    }

    @After
    fun tearDown() {
        // Release the static mock for Log after each test
        mockLog.close()
    }

    @Test
    fun `test transactions update and mappings`() = runTest {
        val user = User("uid1", "test@example.com", "Test User")
        val categoryId = "cat1"
        val tabId = "tab1"
        val savingPocketId = "sp1"

        currentUserFlow.value = user

        // Prepare test category, saving pocket, and tab
        val category = user.id?.let { Category(id = categoryId, name = "Groceries", userId = it) }
        val savingPocket =
            user.id?.let { SavingPocket(id = savingPocketId, name = "Vacation Fund", userID = it) }
        val tab = user.id?.let { Tab(id = tabId, name = "Credit Card", userID = it) }

        categoriesSharedFlow.emit(listOf(category) as List<Category>)
        savingPocketsSharedFlow.emit(listOf(savingPocket) as List<SavingPocket>)
        tabsSharedFlow.emit(listOf(tab) as List<Tab>)

        // Prepare test transactions
        val now = Timestamp.now()

        val transaction1 = user.id?.let {
            Transaction(
                id = "tx1",
                userID = it,
                categoryId = categoryId,
                amount = 100.0,
                date = now,
                type = TransactionType.DEPOSIT,
                description = "Salary"
            )
        }
        val transaction2 = user.id?.let {
            Transaction(
                id = "tx2",
                userID = it,
                categoryId = tabId,
                amount = 50.0,
                date = now,
                type = TransactionType.TAB,
                description = "Credit Card Payment"
            )
        }
        transactionSharedFlow.emit(listOf(transaction1, transaction2) as List<Transaction>)

        // Wait for ViewModel to process the flows
        // Replaced the unreliable delay with advanceUntilIdle()
        advanceUntilIdle()

        // Verify transactions list updated
        assertEquals(2, viewModel.transactions.value.size)

        // Verify sums by type
        val sumByType = viewModel.sumByType.value
        assertEquals(100.0, sumByType[TransactionType.DEPOSIT.name])
        assertEquals(50.0, sumByType[TransactionType.TAB.name])

        // Verify sum by category groups
        val sumByCategory = viewModel.sumByCategory.value
        // Use assertEquals for precise values based on the test data
        assertEquals(100.0, sumByCategory["Income"])
        assertEquals(50.0, sumByCategory["Expenses"])

        // Verify mapping of categoryId to names in currentMonthTransactionsWithName
        val mappedTransactions = viewModel.currentMonthTransactionsWithName.value
        assertTrue(mappedTransactions.any { it.categoryId == "Groceries" })
        assertTrue(mappedTransactions.any { it.categoryId == "Credit Card" })

        // Verify monthlyIncomeExpense is populated
        val monthlyIncomeExpense = viewModel.monthlyIncomeExpense.value
        assertTrue(monthlyIncomeExpense.isNotEmpty())
    }
}
