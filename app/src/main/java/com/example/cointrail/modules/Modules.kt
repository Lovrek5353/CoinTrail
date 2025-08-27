package com.example.cointrail.modules

import AnalyticsViewModel
import CategoriesViewModel
import androidx.room.Room
import com.example.cointrail.database.AppDatabase
import com.example.cointrail.database.StocksDao
import com.example.cointrail.network.KtorClient
import com.example.cointrail.network.StockAPI
import com.example.cointrail.network.StockAPIImpl
import com.example.cointrail.notification.NotificationPreferencesRepository
import com.example.cointrail.notification.NotificationScheduler
import com.example.cointrail.notification.NotificationViewModel
import com.example.cointrail.notification.dataStore
import com.example.cointrail.repository.Repository
import com.example.cointrail.repository.RepositoryImpl
import com.example.cointrail.viewModels.LoginViewModel
import com.example.cointrail.viewModels.MainViewModel
import com.example.cointrail.viewModels.SavingPocketsViewModel
import com.example.cointrail.viewModels.StocksViewModel
import com.example.cointrail.viewModels.TabsViewModel
import com.example.cointrail.viewModels.TransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module {
    single<StockAPI> {
        StockAPIImpl(get())
    }
}

val notificationModule = module {

    single { NotificationPreferencesRepository(get()) }

    single { NotificationScheduler() }

    viewModel { NotificationViewModel(
        preferencesRepository = get(), notificationScheduler = get(),
    ) }
}

val repositoryModule= module{
    single<Repository>{
        RepositoryImpl(get(), get())
    }
}
val dataStoreModule = module {
    single {
        androidContext().dataStore
    }
}

val databaseModule=module{
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "Stock-Database"
        ).build()
    }
    single <StocksDao> {get<AppDatabase>().stocksDao()}
}

val viewModelModule= module {
    viewModel { MainViewModel(repository = get()) }
    viewModel { LoginViewModel(repository = get()) }
    viewModel{CategoriesViewModel(repository = get())}
    viewModel { TabsViewModel(repository = get()) }
    viewModel { SavingPocketsViewModel(repository = get()) }
    viewModel { TransactionViewModel(repository = get()) }
    viewModel { StocksViewModel(repository = get()) }
    viewModel { AnalyticsViewModel(repository = get())}
    viewModel { NotificationViewModel(preferencesRepository = get(), notificationScheduler = get()) }
}

val httpClientModule = module {
    single { KtorClient.httpClient }
}

val appModules= listOf(repositoryModule, viewModelModule, httpClientModule, apiModule, notificationModule, dataStoreModule, databaseModule)

