package com.example.cointrail.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cointrail.MainActivity



object Constants {
    const val NOTIFICATION_ID = 1001
    const val NOTIFICATION_CHANNEL_ID = "daily_reminder_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Daily Reminder"
    const val NOTIFICATION_REQUEST_CODE = 2001
    const val NOTIFICATION_TIME_HOUR_KEY = "notification_time_hour"
    const val NOTIFICATION_TIME_MINUTE_KEY = "notification_time_minute"
    const val NOTIFICATION_ENABLED_KEY = "notification_enabled"
    const val LAST_APP_OPEN_TIMESTAMP_KEY = "last_app_open_timestamp"

    const val NOTIFICATION_ACTION_NO_ACTION_NEEDED = "com.example.cointrail.NO_ACTION_NEEDED"
    const val NOTIFICATION_ACTION_REQUEST_CODE = 2002
}


object NotificationUtils {

    /**
     * Creates a notification channel for Android 8.0 (API level 26) and higher.
     * This is required for notifications to appear on newer Android versions.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.NOTIFICATION_CHANNEL_NAME
            val descriptionText = "Channel for daily reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and displays the notification.
     * When the user taps this notification, it will open the MainActivity.
     * It also includes an action button for "No action needed".
     */
    fun showNotification(context: Context) {
        // Intent to open the MainActivity when the notification body is tapped
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            Constants.NOTIFICATION_REQUEST_CODE,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Intent for the "No action needed" button
        val noActionNeededIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = Constants.NOTIFICATION_ACTION_NO_ACTION_NEEDED
        }
        val noActionNeededPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.NOTIFICATION_ACTION_REQUEST_CODE, // Use a unique request code for this action
            noActionNeededIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Daily Reminder")
            .setContentText("It's time for your daily check-in! Tap to open or acknowledge.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                0,
                "No action needed",
                noActionNeededPendingIntent
            )

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(Constants.NOTIFICATION_ID, builder.build())
            } else {
                Log.w("NotificationUtils", "POST_NOTIFICATIONS permission not granted.")
            }
        }
    }
}