package com.example.cointrail

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cointrail.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompareTwoMonthsTransactionsChartInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun monthlyTransactionsComparison_isVisible() {
        // The MainActivity should be launched already displaying your Composable with test data

        // Check header visible
        composeTestRule.onNodeWithText("Monthly Transactions Comparison").assertIsDisplayed()

        // Check some category bars and labels appear
        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
        composeTestRule.onNodeWithText("Travel").assertIsDisplayed()

        // Check totals row visible
        composeTestRule.onNodeWithText("Total").assertIsDisplayed()
    }
}
