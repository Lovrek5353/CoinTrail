package com.example.cointrail.viewModels

import androidx.lifecycle.ViewModel
import com.example.cointrail.repository.Repository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

class LoginViewModel    (var repository: Repository): ViewModel()
{
    fun emailLogin(email: String, password: String): Flow<Result<AuthResult>> {
        return repository.emailLogin(email, password)
    }
}