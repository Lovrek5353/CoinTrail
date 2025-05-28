package com.example.cointrail

import android.app.Application
import com.example.cointrail.modules.appModules
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CoinTrailApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        startKoin {
            FirebaseFirestore.getInstance()
            androidLogger()
            androidContext(this@CoinTrailApplication)
            modules(appModules)
        }
    }
}