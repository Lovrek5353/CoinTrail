package com.example.cointrail.viewModels

import android.util.Log
import com.example.cointrail.MainDispatcherRule
import com.example.cointrail.data.User
import com.example.cointrail.repository.Repository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // A JUnit Test Rule that sets a TestDispatcher as the main dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // The mock repository dependency
    private lateinit var mockRepository: Repository

    // A mock flow to control the `currentUser` emissions from the repository
    private lateinit var mockCurrentUserFlow: MutableStateFlow<User?>

    // The ViewModel instance we are testing
    private lateinit var viewModel: LoginViewModel

    private val mockLog = mockStatic(Log::class.java)

    @Before
    fun setup() {
        // Initialize the mock repository before each test
        mockRepository = mock()
        mockCurrentUserFlow = MutableStateFlow(null)

        // Set up the mock to return our controlled flow for `currentUser`
        whenever(mockRepository.currentUser).thenReturn(mockCurrentUserFlow)

        // Initialize the ViewModel with the mocked repository
        viewModel = LoginViewModel(mockRepository)

    }

    @After
    fun tearDown() {
        // Release the static mock for Log after each test
        mockLog.close()
    }

    @Test
    fun onEmailChange_updatesEmailState() {
        // Arrange
        val newEmail = "test@example.com"

        // Act
        viewModel.onEmailChange(newEmail)

        // Assert
        assertEquals(newEmail, viewModel.email)
    }

    @Test
    fun onPasswordChange_updatesPasswordState() {
        // Arrange
        val newPassword = "password123"

        // Act
        viewModel.onPasswordChange(newPassword)

        // Assert
        assertEquals(newPassword, viewModel.password)
    }

    @Test
    fun onConfirmPasswordChange_updatesConfirmPasswordState() {
        // Arrange
        val newConfirmPassword = "password123"

        // Act
        viewModel.onConfirmPasswordChange(newConfirmPassword)

        // Assert
        assertEquals(newConfirmPassword, viewModel.confirmPassword)
    }

    @Test
    fun onNameChange_updatesNameState() {
        // Arrange
        val newName = "John Doe"

        // Act
        viewModel.onNameChange(newName)

        // Assert
        assertEquals(newName, viewModel.name)
    }

    @Test
    fun onForgotEmailChange_updatesForgotEmailState() {
        // Arrange
        val newForgotEmail = "forgot@example.com"

        // Act
        viewModel.onForgotEmailChange(newForgotEmail)

        // Assert
        assertEquals(newForgotEmail, viewModel.forgotEmail)
    }

    @Test
    fun signOut_callsRepositorySignOut() {
        // Act
        viewModel.signOut()

        // Assert
        // Verify that the signOut method on the mock repository was called exactly once
        verify(mockRepository, times(1)).signOut()
    }

    @Test
    fun deleteData_callsRepositoryDeleteData_withCorrectUserId() {
        // Arrange
        val userId = "testUserId123"

        // Act
        viewModel.deleteData(userId)

        // Assert
        // Verify that deleteData on the mock repository was called with the correct userId
        verify(mockRepository, times(1)).deleteData(userId)
    }

    @Test
    fun emailLogin_returnsFlowFromRepository() = runTest {
        // Arrange
        val email = "login@example.com"
        val password = "loginpassword"
        // Correctly mock Result<AuthResult> to match the repository signature
        val mockAuthResult = mock<AuthResult>()
        val mockResult = Result.success(mockAuthResult)
        val expectedFlow = flowOf(mockResult)

        // Set up the mock repository to return our expected flow
        whenever(mockRepository.emailLogin(email, password)).thenReturn(expectedFlow)

        // Act
        val actualFlow = viewModel.emailLogin(email, password)

        // Assert
        assertEquals(expectedFlow.first(), actualFlow.first())
    }

    @Test
    fun signInWithGoogle_onSuccess_emitsCorrectEvents() = runTest {
        // Arrange
        val idToken = "some-id-token"
        // Correctly mock Result<User> to match the repository signature
        val successResult = Result.success(mock<User>())
        var googleSignInSuccessEventEmitted = false
        var snackbarMessage: String? = null

        // Mock the repository call to return a successful result
        whenever(mockRepository.signInWithGoogle(idToken)).thenReturn(successResult)

        // Use a coroutine to collect from the eventFlow
        backgroundScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is LoginViewModel.UiEvent.GoogleSignInSuccess -> googleSignInSuccessEventEmitted = true
                    is LoginViewModel.UiEvent.ShowSnackbar -> snackbarMessage = event.message
                    else -> {}
                }
            }
        }

        // Act
        viewModel.signInWithGoogle(idToken)

        // Advance the virtual time until all coroutines are idle
        advanceUntilIdle()

        // Assert
        // Verify the repository function was called with the correct token
        verify(mockRepository).signInWithGoogle(idToken)

        // Assert that the correct events were emitted
        assert(googleSignInSuccessEventEmitted)
        assertEquals("Google sign-in successful!", snackbarMessage)
    }

    @Test
    fun signInWithGoogle_onFailure_emitsSnackbarEvent() = runTest {
        // Arrange
        val idToken = "some-id-token"
        val errorMessage = "Google sign-in failed"
        // Correctly mock Result.failure<User> to match the repository signature
        val failureResult = Result.failure<User>(Exception(errorMessage))
        var snackbarMessage: String? = null

        // Mock the repository call to return a failed result
        whenever(mockRepository.signInWithGoogle(idToken)).thenReturn(failureResult)

        // Collect from the eventFlow
        backgroundScope.launch {
            viewModel.eventFlow.collect { event ->
                if (event is LoginViewModel.UiEvent.ShowSnackbar) {
                    snackbarMessage = event.message
                }
            }
        }

        // Act
        viewModel.signInWithGoogle(idToken)

        // Advance the virtual time
        advanceUntilIdle()

        // Assert
        verify(mockRepository).signInWithGoogle(idToken)
        assertEquals(errorMessage, snackbarMessage)
    }

    @Test
    fun signUp_onSuccess_emitsCorrectEvents() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@user.com"
        val password = "password123"
        val successResult = Result.success(Unit)
        var signUpSuccessEventEmitted = false
        var snackbarMessage: String? = null

        // Mock the ViewModel's state to be ready for the signUp call
        viewModel.onNameChange(name)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        // Mock the repository call for the emailSignUp function
        whenever(mockRepository.emailSignUp(email, password)).thenReturn(successResult)

        // Use a coroutine to collect from the eventFlow
        backgroundScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is LoginViewModel.UiEvent.SignUpSuccess -> signUpSuccessEventEmitted = true
                    is LoginViewModel.UiEvent.ShowSnackbar -> snackbarMessage = event.message
                    else -> {}
                }
            }
        }

        // Act
        // This ensures the coroutine is launched and executed.
        viewModel.signUp()

        // Advance the virtual time until all coroutines are idle
        advanceUntilIdle()

        // Assert
        // Verify the repository function was called with the correct parameters
        verify(mockRepository).emailSignUp(email, password)

        // Assert that the correct events were emitted
        assert(signUpSuccessEventEmitted)
        assertEquals("Sign up successful!", snackbarMessage)
    }

    @Test
    fun sendPasswordResetEmail_onSuccess_emitsCorrectEvents() = runTest {
        // Arrange
        val email = "forgot@example.com"
        val successResult = Result.success(Unit)
        var forgotPasswordSuccessEventEmitted = false
        var snackbarMessage: String? = null

        viewModel.onForgotEmailChange(email)

        // Mock the repository call to return a successful result
        whenever(mockRepository.sendPasswordResetEmail(email)).thenReturn(successResult)

        // Use a coroutine to collect from the eventFlow
        backgroundScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is LoginViewModel.UiEvent.ForgotPasswordSuccess -> forgotPasswordSuccessEventEmitted = true
                    is LoginViewModel.UiEvent.ShowSnackbar -> snackbarMessage = event.message
                    else -> {}
                }
            }
        }

        // Act
        // This ensures the coroutine is launched and executed.
        viewModel.sendPasswordResetEmail()

        // Advance the virtual time until all coroutines are idle
        advanceUntilIdle()

        // Assert
        verify(mockRepository).sendPasswordResetEmail(email)
        assert(forgotPasswordSuccessEventEmitted)
        assertEquals("Password reset link sent to your email!", snackbarMessage)
    }

    @Test
    fun init_collectsCurrentUser_updatesLocalUser() = runTest {
        // Arrange
        val mockUser = User("test-id", "test@user.com", "Test User")

        // Assert initial state before any emissions
        assertNull(viewModel.localUser.value)

        // Act - Simulate a user logging in by emitting a value from the mock flow
        mockCurrentUserFlow.value = mockUser

        // Advance the virtual time to process the flow collection
        advanceUntilIdle()

        // Assert that the localUser StateFlow was updated
        assertNotNull(viewModel.localUser.value)
        assertEquals(mockUser, viewModel.localUser.value)

        // Act - Simulate a user logging out by emitting a null value
        mockCurrentUserFlow.value = null

        // Advance the virtual time
        advanceUntilIdle()

        // Assert the user is now null
        assertNull(viewModel.localUser.value)
    }
}
