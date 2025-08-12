package com.example.cointrail.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val preferencesRepository: NotificationPreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    // StateFlow to expose notification enabled state to UI
    val notificationEnabled: StateFlow<Boolean> = preferencesRepository.notificationEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // StateFlow to expose notification time to UI
    val notificationTime: StateFlow<Pair<Int, Int>> = preferencesRepository.notificationTimeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(9, 0) // Default time
        )

    // StateFlow to expose light theme enabled state to UI
    val isLightTheme: StateFlow<Boolean> = preferencesRepository.isLightThemeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Sets the light theme preference.
     *
     * @param isLight True for light mode, false for dark mode.
     */
    fun setLightTheme(isLight: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setLightTheme(isLight)
        }
    }

    /**
     * Toggles the notification on/off and schedules/cancels the alarm accordingly.
     *
     * @param enabled True to enable, false to disable.
     * @param context The application context.
     */
    fun toggleNotification(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            preferencesRepository.setNotificationEnabled(enabled)
            if (enabled) {
                val (hour, minute) = notificationTime.value
                notificationScheduler.scheduleDailyNotification(context.applicationContext, hour, minute)
            } else {
                notificationScheduler.cancelDailyNotification(context.applicationContext)
            }
        }
    }

    /**
     * Updates the preferred notification time and re-schedules the alarm if enabled.
     *
     * @param hour The new hour.
     * @param minute The new minute.
     * @param context The application context.
     */
    fun updateNotificationTime(hour: Int, minute: Int, context: Context) {
        viewModelScope.launch {
            preferencesRepository.setNotificationTime(hour, minute)
            if (notificationEnabled.value) {
                notificationScheduler.scheduleDailyNotification(context.applicationContext, hour, minute)
            }
        }
    }
}