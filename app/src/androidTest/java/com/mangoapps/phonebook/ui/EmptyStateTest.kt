package com.mangoapps.phonebook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mangoapps.phonebook.core.ui.components.EmptyState
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsContactsEmptyTitle() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                EmptyState(
                    icon = Icons.Default.Phone,
                    title = "No Contacts Found",
                    subtitle = "Your phonebook is empty."
                )
            }
        }

        composeTestRule.onNodeWithText("No Contacts Found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your phonebook is empty.").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsCallLogsEmptyTitle() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                EmptyState(
                    icon = Icons.Default.Call,
                    title = "No Call Logs",
                    subtitle = "No call history available."
                )
            }
        }

        composeTestRule.onNodeWithText("No Call Logs").assertIsDisplayed()
        composeTestRule.onNodeWithText("No call history available.").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsSmsEmptyTitle() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                EmptyState(
                    icon = Icons.Default.Message,
                    title = "No Messages",
                    subtitle = "Your inbox is empty."
                )
            }
        }

        composeTestRule.onNodeWithText("No Messages").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your inbox is empty.").assertIsDisplayed()
    }
}
