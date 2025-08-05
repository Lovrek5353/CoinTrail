package com.example.cointrail.viewModels

import CategoriesViewModel
import com.example.cointrail.MainDispatcherRule
import com.example.cointrail.data.*
import com.example.cointrail.repository.Repository
import com.google.firebase.Timestamp
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf // Keep for other uses if needed
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: Repository
    private lateinit var viewModel: CategoriesViewModel

    private val currentUserFlow = MutableStateFlow<User?>(null)
    private val categoriesSharedFlow = MutableSharedFlow<List<Category>>()
    private val transactionsSharedFlow = MutableSharedFlow<List<Transaction>>() // Added for observeTransactions

    // Mock the static Log class to prevent crashes
    private val mockLog = mockStatic(android.util.Log::class.java)

    @Before
    fun setup() {
        repository = mock()

        // Stub the repository's currentUser flow
        whenever(repository.currentUser).thenReturn(currentUserFlow)
        whenever(repository.getCategories()).thenReturn(categoriesSharedFlow)
        // Stub the repository's getCategoryTransactions to return our controlled SharedFlow
        whenever(repository.getCategoryTransactions(any())).thenReturn(transactionsSharedFlow)


        viewModel = CategoriesViewModel(repository)

    }

    @After
    fun tearDown() {
        mockLog.close()
    }

    @Test
    fun `test onSubmit saves a valid category and resets form`() = runTest {
        // Prepare test data
        val user = User("uid1", "test@example.com", "Test User")
        val categoryName = "Test Category"
        val categoryDescription = "A test description"

        // Emit a user to the flow
        currentUserFlow.value = user

        // Set the form fields
        viewModel.onNameChanged(categoryName)
        viewModel.onDescriptionChanged(categoryDescription)

        // Call the submission function
        viewModel.onSubmit()
        advanceUntilIdle() // Wait for coroutines to complete

        // Verify that the repository's addCategory was called with the correct data
        argumentCaptor<Category>().apply {
            verify(repository).addCategory(capture())
            assertEquals(categoryName, firstValue.name)
            assertEquals(categoryDescription, firstValue.description)
            assertEquals(user.id, firstValue.userId)
        }

        // Verify that the form was reset
        assertEquals("", viewModel.category.name)
        assertEquals("", viewModel.category.description)

        // Verify that the submission success event was emitted
        val event = viewModel.eventFlow.first()
        assert(event is CategoriesViewModel.UiEvent.SubmissionSuccess)
    }

    @Test
    fun `test onSubmit with blank name emits a snackbar event and does not save`() = runTest {
        // Prepare test data
        val user = User("uid1", "test@example.com", "Test User")
        currentUserFlow.value = user

        viewModel.onNameChanged("") // Blank name
        viewModel.onDescriptionChanged("Valid description")

        // Call the submission function
        viewModel.onSubmit()
        advanceUntilIdle()

        // Verify that the repository's addCategory was NOT called
        verify(repository, never()).addCategory(any())

        // Verify that a snackbar event was emitted with the correct message
        val event = viewModel.eventFlow.first()
        assert(event is CategoriesViewModel.UiEvent.ShowSnackbar)
        assertEquals("Category name required", (event as CategoriesViewModel.UiEvent.ShowSnackbar).message)
    }

    @Test
    fun `test onTransactionSubmit saves a valid transaction and resets form`() = runTest {
        // Prepare test data
        val user = User("uid1", "test@example.com", "Test User")
        currentUserFlow.value = user
        val categoryId = "cat1"
        val amount = "100.0"
        val description = "Coffee"
        val dateMillis = System.currentTimeMillis()

        // Set the form fields
        viewModel.onAmountInputChange(amount)
        viewModel.onTransactionDescriptionChange(description)
        viewModel.onTransactionDateSelected(dateMillis)
        viewModel.setTransactionCategory(categoryId)

        // Call the submission function
        viewModel.onTransactionSubmit()
        advanceUntilIdle()

        // Verify that the repository's addTransaction was called with the correct data
        argumentCaptor<Transaction>().apply {
            verify(repository).addTransaction(capture())
            assertEquals(amount.toDouble(), firstValue.amount)
            assertEquals(description, firstValue.description)
            assertEquals(Timestamp(Date(dateMillis)), firstValue.date)
            assertEquals(categoryId, firstValue.categoryId)
            assertEquals(user.id, firstValue.userID)
        }

        // Verify that the form fields were reset
        assertEquals("", viewModel.transactionAmountString)
        assertEquals("", viewModel.transactionDescriptionString)
        assertEquals(null, viewModel.transactionDateMillis)
        assertEquals("", viewModel.transactionCategoryID)
    }

    @Test
    fun `test onTransactionSubmit with invalid amount emits snackbar and does not save`() = runTest {
        // Prepare test data
        val user = User("uid1", "test@example.com", "Test User")
        currentUserFlow.value = user
        val categoryId = "cat1"
        val description = "Valid description"
        val dateMillis = System.currentTimeMillis()

        // Test with blank amount
        viewModel.onAmountInputChange("")
        viewModel.onTransactionDescriptionChange(description)
        viewModel.onTransactionDateSelected(dateMillis)
        viewModel.setTransactionCategory(categoryId)

        viewModel.onTransactionSubmit()
        advanceUntilIdle()

        var event = viewModel.eventFlow.first()
        assert(event is CategoriesViewModel.UiEvent.ShowSnackbar)
        assertEquals("Amount cannot be empty", (event as CategoriesViewModel.UiEvent.ShowSnackbar).message)
        verify(repository, never()).addTransaction(any())

        // Reset the mock
        clearInvocations(repository)

        // Test with zero amount
        viewModel.onAmountInputChange("0.0")
        viewModel.onTransactionSubmit()
        advanceUntilIdle()

        event = viewModel.eventFlow.first()
        assert(event is CategoriesViewModel.UiEvent.ShowSnackbar)
        assertEquals("Invalid amount", (event as CategoriesViewModel.UiEvent.ShowSnackbar).message)
        verify(repository, never()).addTransaction(any())

        // Reset the mock
        clearInvocations(repository)

        // Test with negative amount
        viewModel.onAmountInputChange("-50.0")
        viewModel.onTransactionSubmit()
        advanceUntilIdle()

        event = viewModel.eventFlow.first()
        assert(event is CategoriesViewModel.UiEvent.ShowSnackbar)
        assertEquals("Invalid amount", (event as CategoriesViewModel.UiEvent.ShowSnackbar).message)
        verify(repository, never()).addTransaction(any())
    }

    @Test
    fun `test observeTransactions updates the transactions flow`() = runTest {
        // Prepare test data
        val categoryId = "cat1"
        val txList = listOf(
            Transaction(id = "tx1", categoryId = categoryId),
            Transaction(id = "tx2", categoryId = categoryId)
        )

        // The mock repository is already set up in @Before to return transactionsSharedFlow
        // for getCategoryTransactions(any())

        // Observe the transactions for the given category
        viewModel.observeTransactions(categoryId)
        advanceUntilIdle() // Allow the observeTransactions coroutine to start collecting

        // Emit the test data to the mock flow
        transactionsSharedFlow.emit(txList)
        advanceUntilIdle() // Allow the ViewModel to process the emitted data

        // Verify that the ViewModel's transactions flow was updated with the correct list
        assertEquals(txList, viewModel.transactions.value)
    }

    @Test
    fun `test fetchCategory updates singleCategory flow`() = runTest {
        // Prepare test data
        val categoryId = "cat1"
        val mockCategory = Category(id = categoryId, name = "Test Cat", userId = "user1")
        val singleCategoryFlow = MutableSharedFlow<Category>()

        // Stub the repository to return our controlled SharedFlow for getCategory
        whenever(repository.getCategory(categoryId)).thenReturn(singleCategoryFlow)

        // Fetch the category
        viewModel.fetchCategory(categoryId)
        advanceUntilIdle() // Allow the fetchCategory coroutine to start collecting

        // Emit the test data to the mock flow
        singleCategoryFlow.emit(mockCategory)
        advanceUntilIdle() // Allow the ViewModel to process the emitted data

        // Verify that the ViewModel's singleCategory flow was updated
        assertEquals(mockCategory, viewModel.singleCategory.value)
    }
}
