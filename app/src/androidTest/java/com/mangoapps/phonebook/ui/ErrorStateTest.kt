package com.mangoapps.phonebook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mangoapps.phonebook.core.ui.components.ErrorState
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorState_showsErrorMessage() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                ErrorState(
                    message = "Connection lost. Please check the network and try again.",
                    onRetry = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Connection lost. Please check the network and try again.")
            .assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetryButton() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                ErrorState(message = "Something went wrong", onRetry = {})
            }
        }

        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresCallback() {
        var retryClicked = false
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                ErrorState(
                    message = "Something went wrong",
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue("Retry callback not invoked", retryClicked)
    }

    @Test
    fun errorState_differentMessages_displayCorrectly() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                ErrorState(
                    message = "Failed to load data. Please retry.",
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Failed to load data. Please retry.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
