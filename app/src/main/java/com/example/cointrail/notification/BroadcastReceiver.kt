package com.example.cointrail.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.cointrail.CoinTrailApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class NotificationBroadcastReceiver : BroadcastReceiver(), KoinComponent {
    // Inject the preferences repository
    private val preferencesRepository: NotificationPreferencesRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // We need to goAsync() because onReceive is on the main thread and we'll do suspend calls
        val pendingResult: PendingResult = goAsync()
        val coroutineScope = CoroutineScope(Dispatchers.IO) // Use IO dispatcher for disk operations

        coroutineScope.launch {
            try {
                val lastAppOpenTimestamp = preferencesRepository.lastAppOpenTimestampFlow.first()
                val currentTimestamp = System.currentTimeMillis()

                val lastOpenCalendar = Calendar.getInstance().apply { timeInMillis = lastAppOpenTimestamp }
                val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTimestamp }

                // Check if the last app open date is the same as today's date
                val wasAppOpenedToday = lastOpenCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        lastOpenCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                        lastOpenCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)

                if (!wasAppOpenedToday) {
                    Log.d("NotificationReceiver", "Alarm triggered! App NOT opened today. Showing notification.")
                    withContext(Dispatchers.Main) { // Show notification on Main thread
                        NotificationUtils.showNotification(context)
                    }
                } else {
                    Log.d("NotificationReceiver", "Alarm triggered! App was opened today. Skipping notification.")
                }
            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error checking app open status: ${e.message}")
            } finally {
                pendingResult.finish() // Must call finish() when done with async work
            }
        }
    }
}
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) { // Corrected method name from onOnReceive to onReceive
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ) {
            Log.d("BootReceiver", "Device booted. Re-scheduling notifications.")
            val preferencesRepository = NotificationPreferencesRepository(context.dataStore)
            val notificationScheduler = NotificationScheduler()

            // Use a CoroutineScope to launch a suspend function
            // This is a simplified approach for a BroadcastReceiver.
            // For more complex background work, consider WorkManager.
            // Here, we launch a new coroutine on the IO dispatcher.
            // Note: In a real app, you might want to ensure this doesn't block the main thread
            // or use a dedicated background service/WorkManager for more robust handling.
            val scope = (context.applicationContext as? CoinTrailApplication)?.applicationScope ?: return // Changed to CoinTrailApplication
            scope.launch {
                preferencesRepository.notificationEnabledFlow.collect { isEnabled ->
                    if (isEnabled) {
                        val (hour, minute) = preferencesRepository.notificationTimeFlow.first() // Get current time
                        notificationScheduler.scheduleDailyNotification(context.applicationContext, hour, minute)
                        Log.d("BootReceiver", "Notifications re-scheduled after boot.")
                    } else {
                        Log.d("BootReceiver", "Notifications disabled, not re-scheduling.")
                    }
                    // Stop collecting after the first value to avoid continuous re-scheduling
                    cancel()
                }
            }
        }
    }
}