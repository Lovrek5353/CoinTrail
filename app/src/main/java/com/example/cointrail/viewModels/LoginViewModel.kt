package com.example.cointrail.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cointrail.repository.Repository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel    (var repository: Repository): ViewModel()
{
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

    fun onForgotEmailChange(newEmail: String){
        forgotEmail=newEmail
    }

    fun signUp() {
        viewModelScope.launch {
            // Validation in ViewModel
            when {
                name.isBlank() -> _eventFlow.emit(UiEvent.ShowSnackbar("Name is required"))
                email.isBlank() -> _eventFlow.emit(UiEvent.ShowSnackbar("Email is required"))
                password.isBlank() -> _eventFlow.emit(UiEvent.ShowSnackbar("Password is required"))
                confirmPassword.isBlank() -> _eventFlow.emit(UiEvent.ShowSnackbar("Confirm password is required"))
                password != confirmPassword -> _eventFlow.emit(UiEvent.ShowSnackbar("Passwords don't match"))
                else -> {
                    val result = repository.emailSignUp(email, password)
                    if (result.isSuccess) {
                        _eventFlow.emit(UiEvent.ForgotPasswordSuccess)
                    } else {
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.exceptionOrNull()?.message ?: "Reset password failed"))
                    }
                }
            }
        }
    }

    fun sendPasswordResetEmail() {
        Log.d("LoginViewModel", "sendPasswordResetEmail called with email: $forgotEmail")
        viewModelScope.launch {
            if (forgotEmail.isBlank()) {
                Log.d("LoginViewModel", "Email is blank")
                _eventFlow.emit(UiEvent.ShowSnackbar("Email is required"))
                return@launch
            }
            val result = repository.sendPasswordResetEmail(forgotEmail)
            if (result.isSuccess) {
                Log.d("LoginViewModel", "Password reset email sent successfully")
                _eventFlow.emit(UiEvent.ShowSnackbar("Password reset email sent"))
            } else {
                Log.d("LoginViewModel", "Failed to send reset email")
                _eventFlow.emit(
                    UiEvent.ShowSnackbar(
                        result.exceptionOrNull()?.message ?: "Failed to send reset email"
                    )
                )
            }
        }
    }

    fun signOut() {  //call this on button click and navigate to login screen in the same Unit
        repository.signOut()
        // Optionally emit an event for UI navigation or feedback
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data object SignUpSuccess : UiEvent()
        data object ForgotPasswordSuccess: UiEvent()
    }
    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>> {
        return repository.emailLogin(email, password)
    }
}