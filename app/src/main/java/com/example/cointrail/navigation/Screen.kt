package com.example.cointrail.navigation

sealed class Screen (val route: String) {
    object MainScreen: Screen("main_screen")
    object CategoriesScreen: Screen("categories_screen")
    object TransactionsScreen: Screen("transactions_screen")
    object AccountScreen: Screen("account_screen")

}