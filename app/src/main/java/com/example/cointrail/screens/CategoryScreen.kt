    package com.example.cointrail.screens

    import CategoriesViewModel
    import android.annotation.SuppressLint
    import android.util.Log
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material3.CenterAlignedTopAppBar
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.FloatingActionButton
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.res.dimensionResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.navigation.NavController
    import androidx.navigation.compose.rememberNavController
    import com.example.cointrail.R
    import com.example.cointrail.composables.SmallTransactionsTable
    import com.example.cointrail.composables.SpendingHistogramGraph
    import com.example.cointrail.data.dummyCategories
    import com.example.cointrail.navigation.Screen
    import com.example.cointrail.repository.RepositoryImpl
    import com.example.cointrail.ui.theme.CoinTrailTheme

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CategoryScreen(
        categoryId: String,
        viewModel: CategoriesViewModel,
        navController: NavController,
        modifier: Modifier = Modifier
    ){
        Log.d("CategoryScreen", "Received categoryId: $categoryId")

        LaunchedEffect(categoryId) {
            viewModel.observeTransactions(categoryId)
            viewModel.fetchCategory(categoryId)
        }

        val category= viewModel.singleCategory.collectAsState()
        val transactionList= viewModel.transactions.collectAsState()
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = (category.value?.name.toString()),
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
                                contentDescription = stringResource(id = R.string.emailIcon),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = { //navigate to CategoryScreen editor
                FloatingActionButton(onClick = {
                    navController.navigate(Screen.CategoryTransactionEditorScreen.createRoute(
                        category.value!!.id))
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            },
        )
            {
                LazyColumn {
                    item {
                        Text(
                            text = category.value?.name.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    item{
                        Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding112)))
                    }
                    item{
                        SpendingHistogramGraph(transactionList.value)
                    }
                    item{
                        Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding50)))
                    }
                    item{
                        SmallTransactionsTable(
                            transactionList.value,
                            onTransactionClick = { categoryId, transactionId ->
                                navController.navigate(Screen.TransactionScreen.createRoute(categoryId, transactionId))
                            }
                        )
                    }
                    // add additional details if needed
                }

            }
    }



    @Preview
    @Composable
    fun CategoryScreenPreview() {
        val navController = rememberNavController()
        val viewModel = CategoriesViewModel(repository = RepositoryImpl())
        CoinTrailTheme {
            CategoryScreen(
                categoryId = dummyCategories.first().id,
                viewModel = viewModel,
                navController = navController
            )
        }
    }