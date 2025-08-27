package com.example.cointrail

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.cointrail.composables.CategoryCard
import com.example.cointrail.data.Category
import org.junit.Rule
import org.junit.Test

class CategoryCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun categoryCard_displaysCategoryName() {
        val category = Category("cat1", "Groceries")

        composeTestRule.setContent {
            CategoryCard(category = category, onClick = {})
        }

        composeTestRule.onNodeWithText("Groceries")
            .assertIsDisplayed()
    }

    @Test
    fun categoryCard_clickTriggersCallback() {
        val category = Category("cat2", "Travel")
        var clicked = false

        composeTestRule.setContent {
            CategoryCard(
                category = category,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Travel")
            .performClick()

        assert(clicked) { "Click callback was not triggered." }
    }
}
