import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cointrail.notification.NotificationUtils
import com.example.cointrail.notification.NotificationViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(viewModel: NotificationViewModel = koinViewModel()) { // Use koinViewModel() to get the ViewModel
    val context = LocalContext.current
    // Corrected destructuring: Get the State object first, then its value for destructuring
    val (hour, minute) = viewModel.notificationTime.collectAsState().value
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()


    // State for TimePickerDialog
    val showTimePicker = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Daily Notification Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Enable Daily Notification")
            Switch(
                checked = notificationEnabled,
                onCheckedChange = { isChecked ->
                    viewModel.toggleNotification(isChecked, context)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showTimePicker.value = true },
            enabled = notificationEnabled // Only enable time picker if notifications are enabled
        ) {
            Text("Set Notification Time: %02d:%02d".format(hour, minute))
        }

        // Time Picker Dialog
        if (showTimePicker.value) {
            TimePickerDialog(
                context,
                { _, selectedHour: Int, selectedMinute: Int ->
                    viewModel.updateNotificationTime(selectedHour, selectedMinute, context)
                    showTimePicker.value = false
                },
                hour,
                minute,
                true // is24HourView
            ).show()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Notifications will trigger daily at the set time if enabled.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // For Android 13+ (API 33+), check if notification permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            LaunchedEffect(Unit) {
                // Re-check permission if the app resumes or permission state changes
                hasNotificationPermission.value = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (!hasNotificationPermission.value) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Notification permission is required for Android 13+.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Button(onClick = {
                    // Direct user to app settings to enable notification permission manually
                    val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(settingsIntent)
                }) {
                    Text("Grant Notification Permission")
                }
            }
            Button(
                onClick = {
                    NotificationUtils.showNotification(context)
                }
            ) {
                Text("Show Notification Now (Test)")
            }
        }
    }
}
