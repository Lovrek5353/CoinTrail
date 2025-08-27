package com.example.cointrail

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.cointrail.navigation.Navigation
import com.example.cointrail.navigation.Screen
import com.example.cointrail.notification.NotificationPreferencesRepository
import com.example.cointrail.notification.NotificationUtils
import com.example.cointrail.notification.NotificationViewModel
import com.example.cointrail.ui.theme.CoinTrailTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepository: NotificationPreferencesRepository by inject()

        lifecycleScope.launch {
            preferencesRepository.setLastAppOpenTimestamp(System.currentTimeMillis())
            Log.d("MainActivity", "Last app open timestamp updated.")
        }

        NotificationUtils.createNotificationChannel(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }



            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("MainActivity", "POST_NOTIFICATIONS permission granted.")
                    // Handle permission grant
                } else {
                    Log.w("MainActivity", "POST_NOTIFICATIONS permission denied.")
                }
            }

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            val notificationViewModel: NotificationViewModel = koinViewModel()

            CoinTrailTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    Navigation(startRoute = Screen.WelcomeScreen.route)
                }
            }
        }
    }
}
