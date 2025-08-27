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


    val notificationEnabled: StateFlow<Boolean> = preferencesRepository.notificationEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )


    val notificationTime: StateFlow<Pair<Int, Int>> = preferencesRepository.notificationTimeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(9, 0)
        )

    val isLightTheme: StateFlow<Boolean> = preferencesRepository.isLightThemeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setLightTheme(isLight: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setLightTheme(isLight)
        }
    }

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

    fun updateNotificationTime(hour: Int, minute: Int, context: Context) {
        viewModelScope.launch {
            preferencesRepository.setNotificationTime(hour, minute)
            if (notificationEnabled.value) {
                notificationScheduler.scheduleDailyNotification(context.applicationContext, hour, minute)
            }
        }
    }
}