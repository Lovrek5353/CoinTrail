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
    data object SavingPocketScreen: Screen("saving_pocket_screen/{savingPocketId}") {
        fun createRoute(savingPocketId: String) =
            "saving_pocket_screen/${URLEncoder.encode(savingPocketId, "UTF-8")}"
    }
    data object SavingPocketsScreen: Screen("saving_pockets_screen")
    data object SavingPocketEditorScreen: Screen("saving_pocket_editor_screen")
    data object TabEditorScreen: Screen("tab_editor_screen")
    data object TabsScreen: Screen("tabs_screen")
    data object TabScreen: Screen("tab_screen/{tabId}") {
        fun createRoute(tabId: String) =
            "tab_screen/${URLEncoder.encode(tabId, "UTF-8")}"
    }
    data object WelcomeScreen: Screen("welcome_screen")
    data object MainTransactionEditorScreen: Screen("main_transaction_editor_screen")
    data object CategoryScreen : Screen("category_screen/{categoryId}") {
        fun createRoute(categoryId: String) =
            "category_screen/${URLEncoder.encode(categoryId, "UTF-8")}"
    }
    data object TransactionScreen: Screen("transaction_screen/{categoryID}/{transactionID}"){
        fun createRoute(categoryID: String, transactionID: String) =
            "transaction_screen/${URLEncoder.encode(categoryID, "UTF-8")}/${URLEncoder.encode(transactionID, "UTF-8")}"
    }
    data object SavingPocketTransactionEditor: Screen("saving_pocket_transaction_editor_screen/{savingPocketID}") {
        fun createRoute(savingPocketID: String) =
            "saving_pocket_transaction_editor_screen/${URLEncoder.encode(savingPocketID, "UTF-8")}"
    }
    data object SignUpScreen: Screen("sign_up_screen")
    data object ForgotPasswordScreen: Screen("forgot_password_screen")
}