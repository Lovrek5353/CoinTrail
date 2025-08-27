package com.example.cointrail.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cointrail.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun emailPasswordLogin_showsErrorSnackbar_onInvalidCredentials() {
        // Assume activity is launched with LoginScreen composing

        // Enter email
        composeTestRule.onNodeWithText("Email")
            .performTextInput("invalid@example.com")

        // Enter password
        composeTestRule.onNodeWithText("Password")
            .performTextInput("wrongpassword")

        // Click login button (the trailing icon is a button without a text label)
        composeTestRule.onAllNodes(hasClickAction()).filterToOne(hasParent(hasText("Password"))).performClick()

        // Check if snackbar with "Email login failed" is shown
        composeTestRule.onNodeWithText("Email login failed").assertIsDisplayed()
    }

    @Test
    fun navigateToSignUp_onSignUpButtonClick() {
        // Click the "Sign Up" button to navigate
        composeTestRule.onNodeWithText("Sign Up").performClick()

        // Assert navigation to SignUpScreen by checking some UI shown on that screen
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed() // Example check
    }

    // Additional tests: test Google Sign-In button click, text validation, etc.
}
