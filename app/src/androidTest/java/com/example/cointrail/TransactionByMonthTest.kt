package com.example.cointrail

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.cointrail.composables.TransactionsByMonthGraph
import org.junit.Rule
import org.junit.Test

class TransactionsByMonthGraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val demoData = mapOf(
        "2025-05" to mapOf("Income" to 1200.0, "Expenses" to 950.0),
        "2025-06" to mapOf("Income" to 1700.0, "Expenses" to 1200.0),
        "2025-07" to mapOf("Income" to 2000.0, "Expenses" to 1750.0)
    )

    @Test
    fun `check title and month labels are displayed`() {
        composeTestRule.setContent {
            TransactionsByMonthGraph(data = demoData)
        }

        composeTestRule.onNodeWithText("Income vs Expenses by Month").assertIsDisplayed()
        composeTestRule.onNodeWithText("May 2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jun 2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jul 2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("Income").assertIsDisplayed()
        composeTestRule.onNodeWithText("Expenses").assertIsDisplayed()
    }

    @Test
    fun `tap income bar shows popup`() {
        composeTestRule.setContent {
            TransactionsByMonthGraph(data = demoData)
        }

        val chartNode = composeTestRule.onNodeWithContentDescription("Income Expense by Month Histogram")

        // Tap roughly near "Income" bar of May 2025 (left side)
        chartNode.performTouchInput {
            down(Offset(centerLeft.x + 10f, centerLeft.y - 40f))  // ✅ Correct: pass an Offset object
            up()
        }

        composeTestRule.onNodeWithText("Income\nMay 2025").assertIsDisplayed()
        composeTestRule.onNodeWithText("$1200.00").assertIsDisplayed()
    }

}
