package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.composables.CategoryPieChart
import com.example.cointrail.composables.SpendingLineGraph
import com.example.cointrail.composables.TransactionsTable
import com.example.cointrail.navigation.Screen
import com.example.cointrail.viewModels.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val user by viewModel.user.collectAsState()
    val categorySums by viewModel.resolvedCategorySums.collectAsState()
    val transactions by viewModel.resolvedTransactions.collectAsState()

    if (user == null || categorySums == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val configuration = LocalConfiguration.current
    val chartHeight = configuration.screenHeightDp.dp / 2

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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AccountNotificationScreen.route) }) { //vrati AccountScreen, probaj kombinirat screenove
                        Icon(
                            imageVector = Icons.Default.Person,
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
                    IconButton(onClick = { navController.navigate(Screen.CategoriesScreen.route) }) {
                        Icon(Icons.Filled.Category, contentDescription = "Categories")
                    }
                    IconButton(onClick = { navController.navigate(Screen.SavingPocketsScreen.route) }) {
                        Icon(Icons.Filled.Savings, contentDescription = "Savings")
                    }
                    IconButton(onClick = { navController.navigate(Screen.TabsScreen.route) }) {
                        Icon(Icons.Default.RequestQuote, contentDescription = "Tabs")
                    }
                    IconButton(onClick = { navController.navigate(Screen.StocksScreen.route) }) {
                        Icon(Icons.Default.PieChart, contentDescription = "Portfolio")
                    }
                    IconButton(onClick = { navController.navigate(Screen.AnalyticsScreen.route) }) {
                        Icon(Icons.Default.Analytics, contentDescription = "Profile")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.TransactionEditorScreen.route)
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.addTransaction))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // --- USER GREETING BLOCK ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // "Welcome back" in secondary and semi-bold, slightly bigger
                    Text(
                        text = stringResource(R.string.welcomeBack),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Username prominent
                    Text(
                        text = user!!.name,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- TRANSACTIONS TABLE BLOCK ---
            item {
                Text(
                    text = stringResource(R.string.transactions),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            item {
                TransactionsTable(
                    transactions = transactions,
                    onTransactionClick = { categoryID, transactionID ->
                        val categoryIDForNav = "0"
                        navController.navigate(
                            Screen.TransactionScreen.createRoute(categoryIDForNav, transactionID)
                        )
                    }
                )

            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- SPENDING OVER TIME (LINE GRAPH) ---
            item {
                Text(
                    text = stringResource(R.string.spending_over_time), // Add "Spending Over Time" to your strings.xml
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            item {
                SpendingLineGraph(transactions)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- TOTAL SPENDING BY CATEGORY (PIE CHART SECTION) ---
            item {
                Text(
                    text = stringResource(R.string.total_spending_by_category), //
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            item {
                CategoryPieChart(
                    data = categorySums,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}