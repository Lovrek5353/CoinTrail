package com.example.cointrail.viewModels

import androidx.lifecycle.ViewModel
import com.example.cointrail.data.User
import com.example.cointrail.repository.Repository
import kotlinx.coroutines.flow.StateFlow

class TabsViewModel (
    repository: Repository
): ViewModel() {
    val user: StateFlow<User?> = repository.currentUser

}