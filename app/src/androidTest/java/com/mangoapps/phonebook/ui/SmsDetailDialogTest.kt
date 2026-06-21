package com.mangoapps.phonebook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import com.mangoapps.phonebook.feature.sms.presentation.SmsDetailDialog
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmsDetailDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeSms = SmsMessage(
        id = 2L,
        sender = "Alice Smith",
        body = "Meeting at 3pm tomorrow in conference room B.",
        date = 1_700_000_000_000L
    )

    @Test
    fun smsDetailDialog_showsSenderAsTitle() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsDetailDialog(sms = fakeSms, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Alice Smith").assertIsDisplayed()
    }

    @Test
    fun smsDetailDialog_showsFullMessageBody() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsDetailDialog(sms = fakeSms, onDismiss = {})
            }
        }

        composeTestRule
            .onNodeWithText("Meeting at 3pm tomorrow in conference room B.")
            .assertIsDisplayed()
    }

    @Test
    fun smsDetailDialog_showsMessageLabel() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsDetailDialog(sms = fakeSms, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Message:").assertIsDisplayed()
    }

    @Test
    fun smsDetailDialog_showsTimeLabel() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsDetailDialog(sms = fakeSms, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Time:").assertIsDisplayed()
    }

    @Test
    fun smsDetailDialog_showsCloseButton() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsDetailDialog(sms = fakeSms, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Close").assertIsDisplayed()
    }

    @Test
    fun smsDetailDialog_closeButtonFiresDismiss() {
        var dismissed = false
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsDetailDialog(sms = fakeSms, onDismiss = { dismissed = true })
            }
        }

        composeTestRule.onNodeWithText("Close").performClick()
        assertTrue("onDismiss not called after Close click", dismissed)
    }
}
