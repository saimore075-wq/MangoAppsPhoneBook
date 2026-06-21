package com.mangoapps.phonebook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import com.mangoapps.phonebook.feature.sms.presentation.SmsItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmsItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeSms = SmsMessage(
        id = 1L,
        sender = "John Doe",
        body = "Hello! How are you doing today?",
        date = System.currentTimeMillis()
    )

    @Test
    fun smsItem_showsSenderName() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsItem(sms = fakeSms, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }

    @Test
    fun smsItem_showsMessageBody() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsItem(sms = fakeSms, onClick = {})
            }
        }

        composeTestRule
            .onNodeWithText("Hello! How are you doing today?")
            .assertIsDisplayed()
    }

    @Test
    fun smsItem_clickFiresCallback() {
        var clicked = false
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsItem(sms = fakeSms, onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("John Doe").performClick()
        assertTrue("Click callback not invoked", clicked)
    }

    @Test
    fun smsItem_phoneNumberAsSender_displayed() {
        val smsByNumber = fakeSms.copy(sender = "+1-555-0100", body = "Your OTP is 1234")
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                SmsItem(sms = smsByNumber, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("+1-555-0100").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your OTP is 1234").assertIsDisplayed()
    }
}
