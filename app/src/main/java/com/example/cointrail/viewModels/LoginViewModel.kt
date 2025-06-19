package com.example.cointrail.viewModels

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
                        _eventFlow.emit(UiEvent.SignUpSuccess)
                    } else {
                        _eventFlow.emit(UiEvent.ShowSnackbar(result.exceptionOrNull()?.message ?: "Signup failed"))
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SignUpSuccess : UiEvent()
    }
    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>> {
        return repository.emailLogin(email, password)
    }
}