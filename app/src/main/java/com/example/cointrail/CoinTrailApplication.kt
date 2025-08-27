package com.example.cointrail

import android.app.Application
import com.example.cointrail.modules.appModules
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class CoinTrailApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CoinTrailApplication)

            modules(appModules)
        }
        FirebaseFirestore.getInstance()

    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}