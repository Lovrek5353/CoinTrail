package com.example.cointrail.navigation

import java.net.URLEncoder

sealed class Screen (val route: String) {
    data object MainScreen: Screen("main_screen")
    data object CategoriesScreen: Screen("categories_screen")
    data object TransactionsScreen: Screen("transactions_screen")
    data object AccountScreen: Screen("account_screen")
    data object LoginScreen: Screen("login_screen")
    data object RegistrationScreen: Screen("registration_screen")
    data object CategoryEditorScreen: Screen("category_editor_screen")
    data object TransactionEditorScreen: Screen("transaction_editor_screen")
    data object TransactionScreen: Screen("transaction_screen")
    data object SavingPocketScreen: Screen("saving_pocket_screen")
    data object SavingPocketsScreen: Screen("saving_pockets_screen")
    data object SavingPocketEditorScreen: Screen("saving_pocket_editor_screen")
    data object TabEditorScreen: Screen("tab_editor_screen")
    data object TabsScreen: Screen("tabs_screen")
    data object TabScreen: Screen("tab_screen")
    data object WelcomeScreen: Screen("welcome_screen")
    data object MainTransactionEditorScreen: Screen("main_transaction_editor_screen")
    data object CategoryScreen : Screen("category_screen/{categoryId}") {
        fun createRoute(categoryId: String) =
            "category_screen/${URLEncoder.encode(categoryId, "UTF-8")}"
    }
}