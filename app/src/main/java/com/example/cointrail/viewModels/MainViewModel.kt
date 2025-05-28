package com.example.cointrail.viewModels

import androidx.lifecycle.ViewModel
import com.example.cointrail.repository.Repository

class MainViewModel(
    var repository: Repository
): ViewModel()