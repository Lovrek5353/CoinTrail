package com.example.cointrail

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.cointrail.navigation.Navigation
import com.example.cointrail.navigation.Screen
import com.example.cointrail.notification.NotificationUtils
import com.example.cointrail.notification.NotificationViewModel
import com.example.cointrail.ui.theme.CoinTrailTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Preserved from your original MainActivity

        // 1. Create Notification Channel
        NotificationUtils.createNotificationChannel(applicationContext)

        // 2. Request POST_NOTIFICATIONS permission for Android 13+
        // This launcher handles the permission request result
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission granted.")
                // If permission is granted, and notifications were previously enabled, re-schedule
                // This will be handled by the ViewModel's toggleNotification if the switch is on.
            } else {
                Log.w("MainActivity", "POST_NOTIFICATIONS permission denied.")
                // Explain to the user why the permission is needed
            }
        }

        // Check and request permission on app start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            // Koin will inject the ViewModel for you.
            // This declaration must be inside a @Composable function.
            val notificationViewModel: NotificationViewModel = koinViewModel()

            CoinTrailTheme { // Using your existing theme
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues -> // Added paddingValues parameter
                    // Your existing navigation structure
                    Navigation(startRoute = Screen.WelcomeScreen.route)

                    // You can choose to overlay the NotificationScreen or navigate to it.
                    // For demonstration, here's how you could include it:
                    // If you want to navigate to it, you'd add it as a destination in your Navigation Composable
                    // For now, let's place it in a Box to show it on top for testing,
                    // or you can integrate it into your existing navigation flow.
                    // For example, you might have a settings screen that includes NotificationScreen.
                    // Box(
                    //     modifier = Modifier
                    //         .fillMaxSize()
                    //         .padding(paddingValues) // Apply padding from Scaffold
                    // ) {
                    //     NotificationScreen(viewModel = notificationViewModel)
                    // }
                }
            }
        }
    }
}
