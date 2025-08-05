package com.example.cointrail.notification

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

class NotificationPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    // Keys for DataStore preferences
    private object PreferencesKeys {
        val NOTIFICATION_ENABLED = booleanPreferencesKey(Constants.NOTIFICATION_ENABLED_KEY)
        val NOTIFICATION_TIME_HOUR = intPreferencesKey(Constants.NOTIFICATION_TIME_HOUR_KEY)
        val NOTIFICATION_TIME_MINUTE = intPreferencesKey(Constants.NOTIFICATION_TIME_MINUTE_KEY)
    }

    /**
     * Flow to observe the notification enabled state.
     */
    val notificationEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: false
        }

    /**
     * Flow to observe the notification time (hour and minute).
     */
    val notificationTimeFlow: Flow<Pair<Int, Int>> = dataStore.data
        .map { preferences ->
            val hour = preferences[PreferencesKeys.NOTIFICATION_TIME_HOUR] ?: 9 // Default to 9 AM
            val minute = preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTE] ?: 0 // Default to 0 minutes
            Pair(hour, minute)
        }

    /**
     * Sets the notification enabled state.
     */
    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    /**
     * Sets the notification time.
     */
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME_HOUR] = hour
            preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTE] = minute
        }
    }
}