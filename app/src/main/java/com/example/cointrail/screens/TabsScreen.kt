package com.example.cointrail.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.R
import com.example.cointrail.composables.TabCard
import com.example.cointrail.navigation.Screen
import com.example.cointrail.repository.RepositoryImpl
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.TabsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TabsScreen(
    modifier: Modifier = Modifier,
    viewModel: TabsViewModel,
    navController: NavController
){
    val tabList by viewModel.fetchTabs().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.tabs),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = { //navigate to TabScreen editor
            FloatingActionButton(onClick = {
                navController.navigate(Screen.TabEditorScreen.route)
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.addCTab))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )
    { innerPadding ->
        LazyColumn (
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.padding16)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            items(tabList){ tab ->
                TabCard(tab = tab,
                    onClick = {
                        Log.d("TabsScreen", "Navigating to tab: ${tab.id}")
                        navController.navigate(Screen.TabScreen.createRoute(tab.id))
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun TabsScreenPreview(){
//    val viewModel = TabsViewModel(repository = RepositoryImpl())
//    val navController= rememberNavController()
//    CoinTrailTheme {
//        TabsScreen(
//            navController=navController,
//            viewModel = viewModel
//        )
//    }
}