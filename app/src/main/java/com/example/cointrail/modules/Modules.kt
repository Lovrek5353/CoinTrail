package com.example.cointrail.modules

import CategoriesViewModel
import com.example.cointrail.repository.Repository
import com.example.cointrail.repository.RepositoryImpl
import com.example.cointrail.viewModels.LoginViewModel
import com.example.cointrail.viewModels.MainViewModel
import com.example.cointrail.viewModels.SavingPocketsViewModel
import com.example.cointrail.viewModels.TabsViewModel
import com.example.cointrail.viewModels.TransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule= module{
    single<Repository>{
        RepositoryImpl()
    }
}

val viewModelModule= module {
    viewModel { MainViewModel(repository = get()) }
    viewModel { LoginViewModel(repository = get()) }
    viewModel{CategoriesViewModel(repository = get())}
    viewModel { TabsViewModel(repository = get()) }
    viewModel { SavingPocketsViewModel(repository = get()) }
    viewModel { TransactionViewModel(repository = get()) }
}


val appModules= listOf(repositoryModule, viewModelModule)
