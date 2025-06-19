package com.example.cointrail.viewModels

import androidx.lifecycle.ViewModel
import com.example.cointrail.data.SavingPocket
import com.example.cointrail.data.User
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class SavingsViewModel (
    private val repository: Repository
): ViewModel() {
    val user: StateFlow<User?> = repository.currentUser

    fun fetchSavingPockets(): SharedFlow<List<SavingPocket>>{
        return repository.getSavingPockets()
    }

}