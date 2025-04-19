package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.R
import com.example.cointrail.ui.theme.CoinTrailTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { /* Handle navigation icon click */ }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton( //navigate to settings window
                        onClick = { /* Handle settings icon click */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settingsIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },

        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { /* Navigate to Home */ }) { //navigate to categories
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "temp1"
                        )
                    }
                    IconButton(onClick = { /* Navigate to Savings */ }) { //navigate to savings
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "temp1"
                        )
                    }
                    IconButton(onClick = { /* Navigate to Loans */ }) { // navigate to tabs
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "temp1"
                        )
                    }
                    IconButton(onClick = { /* Navigate to Profile/Settings */ }) { //navigate to portfolio
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "temp5"
                        )
                    }
                    IconButton(onClick = { /* Navigate to Profile/Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "temp5"
                        )
                    }
                }
            }

        }
    ) { innerPadding ->
        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(
                text = stringResource(R.string.welcomeBack)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.name)
            )
        }
    }
}

@Preview
@Composable
fun MainScreenPreview(){
    CoinTrailTheme {
        MainScreen()
    }
}