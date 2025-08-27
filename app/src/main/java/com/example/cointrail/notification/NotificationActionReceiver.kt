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

class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {
    private val preferencesRepository: NotificationPreferencesRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult: PendingResult = goAsync()
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            try {
                when (intent.action) {
                    Constants.NOTIFICATION_ACTION_NO_ACTION_NEEDED -> {
                        Log.d("NotificationActionReceiver", "No action needed button tapped!")
                        preferencesRepository.setLastAppOpenTimestamp(System.currentTimeMillis())
                        Log.d("NotificationActionReceiver", "Last app open timestamp updated due to 'No action needed'.")

                        withContext(Dispatchers.Main) {
                            NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_ID)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationActionReceiver", "Error handling notification action: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
}