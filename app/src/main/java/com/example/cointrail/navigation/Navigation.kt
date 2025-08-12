package com.example.cointrail.navigation

import AnalyticsViewModel
import CategoriesViewModel
import CategoryEditorScreen
import NotificationScreen
import SignUpScreen
import StockDetailsScreen
import TransactionScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cointrail.notification.NotificationViewModel
import com.example.cointrail.screens.AnalyticsScreen
import com.example.cointrail.screens.AccountScreen
import com.example.cointrail.screens.AssetSearchScreen
import com.example.cointrail.screens.CategoriesScreen
import com.example.cointrail.screens.CategoryScreen
import com.example.cointrail.screens.CategoryTransactionEditorScreen
import com.example.cointrail.screens.ForgotPasswordScreen
import com.example.cointrail.screens.LoginScreen
import com.example.cointrail.screens.MainScreen
import com.example.cointrail.screens.PortfolioScreen
import com.example.cointrail.screens.SavingPocketEditorScreen
import com.example.cointrail.screens.SavingPocketScreen
import com.example.cointrail.screens.SavingPocketTransactionEditor
import com.example.cointrail.screens.SavingPocketsScreen
import com.example.cointrail.screens.StockEditorScreen
import com.example.cointrail.screens.StocksScreen
import com.example.cointrail.screens.TabEditorScreen
import com.example.cointrail.screens.TabScreen
import com.example.cointrail.screens.TabTransactionEditor
import com.example.cointrail.screens.TabsScreen
import com.example.cointrail.screens.TransactionEditorScreen
import com.example.cointrail.screens.UpdateTransactionEditorScreen
import com.example.cointrail.screens.WelcomeScreen

import com.example.cointrail.viewModels.LoginViewModel
import com.example.cointrail.viewModels.MainViewModel
import com.example.cointrail.viewModels.SavingPocketsViewModel
import com.example.cointrail.viewModels.StocksViewModel
import com.example.cointrail.viewModels.TabsViewModel
import com.example.cointrail.viewModels.TransactionViewModel
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder

@Composable
fun Navigation(startRoute: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(
                navController = navController,
                viewModel = koinViewModel<MainViewModel>()
            )
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(
                navController = navController,
               viewModel = koinViewModel<LoginViewModel>()
            )
        }
        composable(route = Screen.CategoriesScreen.route) {
            CategoriesScreen(
                navController = navController,
                viewModel = koinViewModel<CategoriesViewModel>()
            )
        }
        composable(route = Screen.CategoryEditorScreen.route) {
            CategoryEditorScreen(
                navController = navController,
                viewModel = koinViewModel<CategoriesViewModel>()
            )
        }
        composable(route = Screen.TransactionEditorScreen.route) {
            TransactionEditorScreen(
                navController = navController,
                viewModel = koinViewModel<MainViewModel>()
            )
        }
        composable(
            route = Screen.TransactionScreen.route,
            arguments = listOf(
                navArgument("categoryID") { type = NavType.StringType },
                navArgument("transactionID") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedCategoryId = backStackEntry.arguments?.getString("categoryID") ?: ""
            val categoryId = URLDecoder.decode(encodedCategoryId, "UTF-8")
            val encodedTransactionId = backStackEntry.arguments?.getString("transactionID") ?: ""
            val transactionId = URLDecoder.decode(encodedTransactionId, "UTF-8")

            TransactionScreen(
                navController = navController,
                categoryID = categoryId,
                transactionID = transactionId,
                viewModel = koinViewModel<TransactionViewModel>()
            )
        }
        composable(route = Screen.TabsScreen.route) {
            TabsScreen(
                navController = navController,
                viewModel = koinViewModel<TabsViewModel>()
            )
        }
        composable(route = Screen.TabEditorScreen.route) {
            TabEditorScreen(
                navController = navController,
                viewModel = koinViewModel<TabsViewModel>()
            )
        }
        composable(route = Screen.SavingPocketsScreen.route) {
            SavingPocketsScreen(
                navController = navController,
                viewModel = koinViewModel<SavingPocketsViewModel>()
            )
        }
        composable(
            route = Screen.CategoryScreen.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryId = URLDecoder.decode(encodedId, "UTF-8")
            CategoryScreen(
                categoryId = categoryId,
                viewModel = koinViewModel<CategoriesViewModel>(),
                navController = navController
            )
        }
        composable(
            route = Screen.TabScreen.route,
            arguments = listOf(navArgument("tabId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("tabId") ?: ""
            val tabId = URLDecoder.decode(encodedId, "UTF-8")
            TabScreen(
                tabID = tabId,
                viewModel = koinViewModel<TabsViewModel>(),
                navController = navController
            )
        }
        composable(
            route = Screen.SavingPocketScreen.route,
            arguments = listOf(navArgument("savingPocketId") { type = NavType.StringType })
        ) {
            val encodedId = it.arguments?.getString("savingPocketId") ?: ""
            val savingPocketId = URLDecoder.decode(encodedId, "UTF-8")
            SavingPocketScreen(
                savingPocketId = savingPocketId,
                viewModel = koinViewModel<SavingPocketsViewModel>(),
                navController = navController
            )
        }
        composable(route = Screen.SavingPocketEditorScreen.route) {
            SavingPocketEditorScreen(
                navController = navController,
                viewModel = koinViewModel<SavingPocketsViewModel>()
            )
        }
        composable(
            route = Screen.SavingPocketTransactionEditor.route,
            arguments = listOf(navArgument("savingPocketID") { type = NavType.StringType })
        ) {
            val encodedId = it.arguments?.getString("savingPocketID") ?: ""
            val savingPocketID = URLDecoder.decode(encodedId, "UTF-8")
            SavingPocketTransactionEditor(
                viewModel = koinViewModel<SavingPocketsViewModel>(),
                navController = navController,
                savingPocketID = savingPocketID
            )
        }
        composable(
            route=Screen.TabTransactionEditorScreen.route,
            arguments = listOf(navArgument("tabID") { type = NavType.StringType })
        ){
            val encodedId = it.arguments?.getString("tabID") ?: ""
            val tabID = URLDecoder.decode(encodedId, "UTF-8")
            TabTransactionEditor(
                viewModel = koinViewModel<TabsViewModel>(),
                navController = navController,
                tabID = tabID
            )
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(
                viewModel = koinViewModel<LoginViewModel>(),
                onSignUpSuccess = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(
                viewModel = koinViewModel<LoginViewModel>(),
                onForgotPasswordClick = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.ForgotPasswordScreen.route) { inclusive = true }
                    }}
            )
        }
        composable(route=Screen.CategoryTransactionEditorScreen.route,
            arguments = listOf(navArgument("categoryID") { type = NavType.StringType })
        ){
            val encodedId = it.arguments?.getString("categoryID") ?: ""
            val categoryID = URLDecoder.decode(encodedId, "UTF-8")
            CategoryTransactionEditorScreen(
                viewModel = koinViewModel<CategoriesViewModel>(),
                navController = navController,
                categoryId = categoryID
            )
        }
        composable(route = Screen.StocksScreen.route) {
            StocksScreen(
                viewModel = koinViewModel<StocksViewModel>(),
                navController = navController
            )
        }
        composable(route=Screen.UpdateTransactionEditorScreen.route,
            arguments = listOf(navArgument("transactionID") { type = NavType.StringType })) {
            val encodedId = it.arguments?.getString("transactionID") ?: ""
            val transactionID = URLDecoder.decode(encodedId, "UTF-8")
            UpdateTransactionEditorScreen(
                viewModel = koinViewModel<TransactionViewModel>(),
                navController = navController,
                transactionID = transactionID
            )
        }
        composable(route=Screen.AssetSearchScreen.route){
            AssetSearchScreen(
                viewModel = koinViewModel<StocksViewModel>(),
                navController = navController
            )
        }
        composable(route= Screen.StockDetailsScreen.route,
            arguments = listOf(navArgument("stockSymbol") { type = NavType.StringType })) {
            val encodedId = it.arguments?.getString("stockSymbol") ?: ""
            val stockSymbol = URLDecoder.decode(encodedId, "UTF-8")
            StockDetailsScreen(
                stockSymbol = stockSymbol,
                viewModel = koinViewModel<StocksViewModel>(),
                navController = navController
            )
        }
        composable(route=Screen.AnalyticsScreen.route) {
            AnalyticsScreen(
                viewModel = koinViewModel<AnalyticsViewModel>(),
                navController = navController
            )
        }
        composable(route=Screen.AccountScreen.route) {
            AccountScreen(
                viewModel = koinViewModel<LoginViewModel>(),
                navController=navController,
                onNameEditClick = {
                    navController.navigate(Screen.AccountEditorScreen.route) {
                    }
                }
            )
        }
        composable(route=Screen.PortfolioScreen.route) {
            PortfolioScreen(
                navController = navController,
                viewModel = koinViewModel<StocksViewModel>()
            )
        }
        composable(route=Screen.WelcomeScreen.route) {
            WelcomeScreen(
                navController = navController,
            )
        }
        composable(route=Screen.NotificationScreen.route) {
            NotificationScreen(viewModel = koinViewModel<NotificationViewModel>())
        }
        composable(route=Screen.StockEditorScreen.route,
            arguments = listOf(navArgument("stockSymbol") { type = NavType.StringType })){
            val encodedId = it.arguments?.getString("stockSymbol") ?: ""
            val stockSymbol = URLDecoder.decode(encodedId, "UTF-8")
            StockEditorScreen(
                viewModel = koinViewModel<StocksViewModel>(),
                navController = navController,
                stockSymbol = stockSymbol
            )
        }
    }
}
