import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
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
fun NotificationScreen(
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val (hour, minute) = viewModel.notificationTime.collectAsState().value
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val isLightTheme by viewModel.isLightTheme.collectAsState()

    val showTimePicker = remember { mutableStateOf(false) }

    val hasNotificationPermission = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(Unit) {
            hasNotificationPermission.value = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Card(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Light Mode",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isLightTheme,
                        onCheckedChange = {
                            viewModel.setLightTheme(it)
                        }
                    )
                }
            }

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Card(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Enable daily notification",
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.toggleNotification(isChecked, context)
                            }
                        )
                    }

                    Button(
                        onClick = { showTimePicker.value = true },
                        enabled = notificationEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Notification Time: %02d:%02d".format(hour, minute))
                    }

                    if (showTimePicker.value) {
                        TimePickerDialog(
                            context,
                            { _, selectedHour: Int, selectedMinute: Int ->
                                viewModel.updateNotificationTime(selectedHour, selectedMinute, context)
                                showTimePicker.value = false
                            },
                            hour,
                            minute,
                            true
                        ).show()
                    }

                    Text(
                        text = "A reminder will trigger daily at the selected time (if enabled).",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission.value) {
                        Text(
                            text = "Notification permission is required (Android 13+).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = {
                                val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(settingsIntent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant Notification Permission")
                        }
                    }
                    OutlinedButton(
                        onClick = { NotificationUtils.showNotification(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show Notification Now (Test)")
                    }
                }
            }
        }
    }
}