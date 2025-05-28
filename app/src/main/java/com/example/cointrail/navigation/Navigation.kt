package com.example.cointrail.navigation

import CategoriesViewModel
import CategoryEditorScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cointrail.screens.CategoriesScreen
import com.example.cointrail.screens.CategoryScreen
import com.example.cointrail.screens.LoginScreen
import com.example.cointrail.screens.MainScreen
import com.example.cointrail.viewModels.LoginViewModel
import com.example.cointrail.viewModels.MainViewModel
import org.koin.androidx.compose.get
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
                viewModel = get<MainViewModel>()
            )
        }
        composable(route=Screen.LoginScreen.route) {
            LoginScreen(
                navController = navController,
                viewModel = get<LoginViewModel>()
            )
        }
        composable(route=Screen.CategoriesScreen.route) {
            CategoriesScreen(
                navController = navController,
                viewModel = get<CategoriesViewModel>()
            )
        }
        composable(route=Screen.CategoryEditorScreen.route) {
            CategoryEditorScreen(
                navController = navController,
                viewModel = get<CategoriesViewModel>()
            )
        }
        composable(
            route = Screen.CategoryScreen.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryId = URLDecoder.decode(encodedId, "UTF-8")
            CategoryScreen(categoryId = categoryId, viewModel = get<CategoriesViewModel>(), navController = navController)
        }
    }
}