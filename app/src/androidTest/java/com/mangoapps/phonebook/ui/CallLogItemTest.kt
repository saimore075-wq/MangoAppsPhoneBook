package com.mangoapps.phonebook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import com.mangoapps.phonebook.feature.calllogs.domain.model.CallLog
import com.mangoapps.phonebook.feature.calllogs.domain.model.CallType
import com.mangoapps.phonebook.feature.calllogs.presentation.CallLogItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CallLogItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val incomingCall = CallLog(
        id = 1L,
        name = "Bob Johnson",
        number = "+1-555-0200",
        duration = 125L,
        date = System.currentTimeMillis(),
        type = CallType.INCOMING,
        simLabel = null
    )

    private val outgoingCall = CallLog(
        id = 2L,
        name = null,
        number = "+1-555-0300",
        duration = 0L,
        date = System.currentTimeMillis(),
        type = CallType.OUTGOING,
        simLabel = null
    )

    private val missedCall = CallLog(
        id = 3L,
        name = "Carol White",
        number = "+1-555-0400",
        duration = 0L,
        date = System.currentTimeMillis(),
        type = CallType.MISSED,
        simLabel = "SIM 1"
    )

    @Test
    fun callLogItem_incoming_showsCallerName() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = incomingCall)
            }
        }

        composeTestRule.onNodeWithText("Bob Johnson").assertIsDisplayed()
    }

    @Test
    fun callLogItem_incoming_showsDuration() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = incomingCall)
            }
        }

        composeTestRule.onNodeWithText("2m 5s").assertIsDisplayed()
    }

    @Test
    fun callLogItem_outgoing_showsNumberWhenNoName() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = outgoingCall)
            }
        }

        composeTestRule.onNodeWithText("+1-555-0300").assertIsDisplayed()
    }

    @Test
    fun callLogItem_outgoing_notConnected_showsLabel() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = outgoingCall)
            }
        }

        composeTestRule.onNodeWithText("Not connected").assertIsDisplayed()
    }

    @Test
    fun callLogItem_missed_showsCallerName() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = missedCall)
            }
        }

        composeTestRule.onNodeWithText("Carol White").assertIsDisplayed()
    }

    @Test
    fun callLogItem_missed_showsSimLabel() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = missedCall)
            }
        }

        composeTestRule.onNodeWithText("SIM 1").assertIsDisplayed()
    }

    @Test
    fun callLogItem_clickDoesNotCrash() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                CallLogItem(log = incomingCall)
            }
        }

        composeTestRule.onNodeWithText("Bob Johnson").performClick()
    }
}
