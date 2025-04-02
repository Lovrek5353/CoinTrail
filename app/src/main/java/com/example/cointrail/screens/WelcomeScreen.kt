package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.R
import com.example.cointrail.ui.theme.CoinTrailTheme


@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.background, // Base background color
                        MaterialTheme.colorScheme.surface     // Slightly elevated background color
                    )
                )
            )
            .padding(16.dp), // Add padding for better spacing
        verticalArrangement = Arrangement.Center, // Center content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
    ) {
        // App Icon with Tertiary Color Tint
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = stringResource(id = R.string.appIcon),
            modifier = Modifier
                .fillMaxWidth(0.5f) // Use 50% of screen width
                .aspectRatio(1f), // Maintain square aspect ratio
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary) // Apply tertiary color tint
        )

        Spacer(modifier = Modifier.height(16.dp)) // Add to sizes.xml

        // Welcome Text with Secondary Color
        Text(
            text = stringResource(id = R.string.welcomeMessage),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.secondary // Use secondary color for text
        )

        Spacer(modifier = Modifier.height(16.dp)) // Add to sizes.xml

        // Login Button with Primary Container Color
        Button(
            onClick = { /* Handle login click */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(
                text = stringResource(id = R.string.login),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add to sizes.xml

        // Get Started Button with Secondary Container Color
        Button(
            onClick = { /* Handle get started click */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                text = stringResource(id = R.string.getStarted),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}




@Preview
@Composable
fun WelcomeScreenPreview(){
    CoinTrailTheme {
        WelcomeScreen()
    }
}