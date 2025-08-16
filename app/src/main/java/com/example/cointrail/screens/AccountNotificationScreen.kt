package com.example.cointrail.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.navigation.Screen
import com.example.cointrail.notification.NotificationUtils
import com.example.cointrail.notification.NotificationViewModel
import com.example.cointrail.viewModels.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAndNotificationScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    onNameEditClick: () -> Unit,
) {
    val user = loginViewModel.localUser.collectAsState().value
    val context = LocalContext.current

    val (hour, minute) = notificationViewModel.notificationTime.collectAsState().value
    val notificationEnabled by notificationViewModel.notificationEnabled.collectAsState()
    val isLightTheme by notificationViewModel.isLightTheme.collectAsState()
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

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.no_user_found))
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // -------- Account Screen content --------
            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding24)))
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.padding96))
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile_icon),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.padding56))
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding16)))
            }

            item {
                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding8),
                            bottom = dimensionResource(R.dimen.padding8)
                        )
                )
            }

            item {
                Divider(
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding16),
                        end = dimensionResource(R.dimen.padding16)
                    ),
                    thickness = dimensionResource(R.dimen.padding1),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

            item {
                Text(
                    text = stringResource(R.string.name),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding16)
                        )
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            end = dimensionResource(R.dimen.padding16),
                            bottom = dimensionResource(R.dimen.padding16)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = onNameEditClick,
                        modifier = Modifier.size(dimensionResource(R.dimen.padding24))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_name),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.email),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            top = dimensionResource(R.dimen.padding8)
                        )
                )
            }

            item {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(R.dimen.padding16),
                            bottom = dimensionResource(R.dimen.padding16)
                        )
                )
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding32)))
            }

            item {
                Button(
                    onClick = {
                        loginViewModel.signOut()
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.AccountScreen.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding16))
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding16)))
            }

            item {
                Button(
                    onClick = {
                        loginViewModel.deleteData(userID = user.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding16))
                ) {
                    Text(text = stringResource(R.string.delete_data))
                }
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding32)))
            }

            // -------- Notification Section Header --------
            item {
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding16), vertical = dimensionResource(R.dimen.padding16))
                )
            }

            // -------- Notification Screen content --------
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.padding16)),
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
                                    notificationViewModel.setLightTheme(it)
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
                                        notificationViewModel.toggleNotification(isChecked, context)
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
                                        notificationViewModel.updateNotificationTime(selectedHour, selectedMinute, context)
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
    }
}
