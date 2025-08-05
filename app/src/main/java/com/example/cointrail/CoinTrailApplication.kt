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
// import com.google.firebase.firestore.FirebaseFirestore // You'd import this if you were directly using it here

class CoinTrailApplication : Application() {
    // This scope can be used for application-wide coroutines,
    // especially for background tasks that need to outlive a single activity.
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // Start Koin for dependency injection
        startKoin {
            // Log Koin messages for debugging (optional, set to Level.ERROR for production)
            androidLogger(Level.ERROR)
            // Provide the application context to Koin
            androidContext(this@CoinTrailApplication)
            // Load your Koin modules
            modules(appModules) // Assuming 'appModules' is a list of your Koin modules
        }
        FirebaseFirestore.getInstance()
        // If you need to initialize Firebase generally (e.g., for analytics, crashlytics)
        // that doesn't necessarily need to be injected, you can do it here.
        // FirebaseApp.initializeApp(this) // This is usually done automatically with google-services.json
    }

    override fun onTerminate() {
        super.onTerminate()
        // Cancel the application scope when the application terminates
        applicationScope.cancel()
    }
}