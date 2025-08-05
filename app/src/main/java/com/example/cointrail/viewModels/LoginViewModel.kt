package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.data.User
import com.example.cointrail.repository.Repository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(var repository: Repository) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var name by mutableStateOf("")
        private set

    var forgotEmail by mutableStateOf("")
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChange(newConfirm: String) {
        confirmPassword = newConfirm
    }

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onForgotEmailChange(newEmail: String) {
        forgotEmail = newEmail
    }

    fun signUp() {
        viewModelScope.launch {
            // ... your existing signUp logic ...
        }
    }

    fun sendPasswordResetEmail() {
        // ... your existing sendPasswordResetEmail logic ...
    }

    fun signOut() {
        repository.signOut()
    }

    private val _localUser = MutableStateFlow<User?>(null)
    val localUser: StateFlow<User?> = _localUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collectLatest { user ->
                _localUser.value = user
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        Log.d("LoginViewModel", "Launching signInWithGoogle with ID token")
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogle(idToken)
                if (result.isSuccess) {
                    Log.d("LoginViewModel", "Google sign-in successful")
                    _eventFlow.emit(UiEvent.GoogleSignInSuccess)  // <-- ADD THIS LINE
                    _eventFlow.emit(UiEvent.ShowSnackbar("Google sign-in successful!"))
                } else {
                    Log.w("LoginViewModel", "Google sign-in failed: ${result.exceptionOrNull()?.message}")
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.exceptionOrNull()?.message ?: "Google sign-in failed"))
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during signInWithGoogle coroutine", e)
                _eventFlow.emit(UiEvent.ShowSnackbar("Sign in error: ${e.message}"))
            }
        }
    }



    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object GoogleSignInSuccess : UiEvent()
        object SignUpSuccess : UiEvent()
        object ForgotPasswordSuccess : UiEvent()
    }

    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>> {
        return repository.emailLogin(email, password)
    }

    fun deleteData(userID: String?) {
        repository.deleteData(userID)
    }
}
