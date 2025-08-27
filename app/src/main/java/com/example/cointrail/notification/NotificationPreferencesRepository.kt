package com.example.cointrail.notification

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

class NotificationPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val NOTIFICATION_ENABLED = booleanPreferencesKey(Constants.NOTIFICATION_ENABLED_KEY)
        val NOTIFICATION_TIME_HOUR = intPreferencesKey(Constants.NOTIFICATION_TIME_HOUR_KEY)
        val NOTIFICATION_TIME_MINUTE = intPreferencesKey(Constants.NOTIFICATION_TIME_MINUTE_KEY)
        val LAST_APP_OPEN_TIMESTAMP = longPreferencesKey(Constants.LAST_APP_OPEN_TIMESTAMP_KEY)
        val IS_LIGHT_THEME = booleanPreferencesKey("is_light_theme")
    }

    val notificationEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: false
        }

    val notificationTimeFlow: Flow<Pair<Int, Int>> = dataStore.data
        .map { preferences ->
            val hour = preferences[PreferencesKeys.NOTIFICATION_TIME_HOUR] ?: 9
            val minute = preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTE] ?: 0
            Pair(hour, minute)
        }

    val lastAppOpenTimestampFlow: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_APP_OPEN_TIMESTAMP] ?: 0L
        }

    val isLightThemeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_LIGHT_THEME] ?: false
        }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME_HOUR] = hour
            preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTE] = minute
        }
    }

    suspend fun setLastAppOpenTimestamp(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_APP_OPEN_TIMESTAMP] = timestamp
        }
    }

    suspend fun setLightTheme(isLight: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LIGHT_THEME] = isLight
        }
    }
}