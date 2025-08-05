package com.example.cointrail.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class NotificationScheduler {

    /**
     * Schedules a daily repeating alarm at the specified hour and minute.
     * The alarm will trigger the NotificationBroadcastReceiver.
     *
     * @param context The application context.
     * @param hour The hour (0-23) for the notification.
     * @param minute The minute (0-59) for the notification.
     */
    fun scheduleDailyNotification(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set the calendar to the desired time today
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the set time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        Log.d("NotificationScheduler", "Scheduling notification for: ${calendar.time}")

        // Schedule the alarm to repeat daily
        // RTC_WAKEUP wakes up the device to fire the alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat every day
            pendingIntent
        )
    }

    fun cancelDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE // Use NO_CREATE to check if it exists
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            Log.d("NotificationScheduler", "Daily notification alarm cancelled.")
        } ?: run {
            Log.d("NotificationScheduler", "No daily notification alarm to cancel.")
        }
    }

}