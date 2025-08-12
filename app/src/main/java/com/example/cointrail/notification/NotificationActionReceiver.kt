package com.example.cointrail.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- NEW: NotificationActionReceiver.kt (Dedicated Receiver for Notification Actions) ---
class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {
    // Inject the preferences repository
    private val preferencesRepository: NotificationPreferencesRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        // We need to goAsync() because onReceive is on the main thread and we'll do suspend calls
        val pendingResult: PendingResult = goAsync()
        val coroutineScope = CoroutineScope(Dispatchers.IO) // Use IO dispatcher for disk operations

        coroutineScope.launch {
            try {
                when (intent.action) {
                    Constants.NOTIFICATION_ACTION_NO_ACTION_NEEDED -> {
                        Log.d("NotificationActionReceiver", "No action needed button tapped!")
                        // Update the last app open timestamp to today, effectively acknowledging the notification
                        preferencesRepository.setLastAppOpenTimestamp(System.currentTimeMillis())
                        Log.d("NotificationActionReceiver", "Last app open timestamp updated due to 'No action needed'.")

                        // Dismiss the notification from the shade
                        withContext(Dispatchers.Main) {
                            NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_ID)
                        }
                    }
                    // Add more actions here if you have other notification buttons
                }
            } catch (e: Exception) {
                Log.e("NotificationActionReceiver", "Error handling notification action: ${e.message}")
            } finally {
                pendingResult.finish() // Must call finish() when done with async work
            }
        }
    }
}