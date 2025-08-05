package com.example.cointrail.composables

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.enums.TransactionType
import com.google.firebase.Timestamp
import org.junit.Rule
import org.junit.Test
import java.util.*

class TransactionsTableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `TransactionsTable displays headers and transactions correctly`() {
        val transaction1 = Transaction(
            id = "tx1",
            userID = "user1",
            categoryId = "Groceries",
            amount = 50.0,
            date = Timestamp(Date(System.currentTimeMillis() - 86400000)),
            description = "Milk and bread",
            type = TransactionType.WITHDRAWAL
        )
        val transaction2 = Transaction(
            id = "tx2",
            userID = "user1",
            categoryId = "Salary",
            amount = 1500.0,
            date = Timestamp(Date()),
            description = "Monthly Paycheck",
            type = TransactionType.DEPOSIT
        )

        composeTestRule.setContent {
            TransactionsTable(transactions = listOf(transaction1, transaction2))
        }

        composeTestRule.onNodeWithText("Date").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amount").assertIsDisplayed()

        composeTestRule.onNodeWithText("Milk and bread").assertIsDisplayed()
        composeTestRule.onNodeWithText("Groceries").assertIsDisplayed()
        composeTestRule.onNodeWithText("50.00").assertIsDisplayed()

        composeTestRule.onNodeWithText("Monthly Paycheck").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salary").assertIsDisplayed()
        composeTestRule.onNodeWithText("1500.00").assertIsDisplayed()
    }

    @Test
    fun `TransactionsTable handles empty transactions list gracefully`() {
        composeTestRule.setContent {
            TransactionsTable(transactions = emptyList())
        }

        composeTestRule.onNodeWithText("Date").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()

        composeTestRule.onNodeWithText("Milk and bread", ignoreCase = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("50.00").assertDoesNotExist()
    }

    @Test
    fun `TransactionsTable row click triggers callback`() {
        val transaction = Transaction(
            id = "tx1",
            userID = "user1",
            categoryId = "Utilities",
            amount = 75.0,
            date = Timestamp(Date()),
            description = "Electricity Bill",
            type = TransactionType.WITHDRAWAL
        )

        var clickedCategoryId: String? = null
        var clickedTransactionId: String? = null

        composeTestRule.setContent {
            TransactionsTable(
                transactions = listOf(transaction),
                onTransactionClick = { categoryId, transactionId ->
                    clickedCategoryId = categoryId
                    clickedTransactionId = transactionId
                }
            )
        }

        composeTestRule.onNodeWithText("Electricity Bill").performClick()

        assert(clickedCategoryId == transaction.categoryId)
        assert(clickedTransactionId == transaction.id)
    }
}
