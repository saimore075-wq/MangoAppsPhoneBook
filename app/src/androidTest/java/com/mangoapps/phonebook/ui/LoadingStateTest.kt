package com.mangoapps.phonebook.ui

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mangoapps.phonebook.core.ui.components.LoadingState
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadingStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_rendersWithoutCrash() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                LoadingState()
            }
        }

        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun loadingState_showsCircularProgressIndicator() {
        composeTestRule.setContent {
            MangoAppsPhoneBookTheme {
                LoadingState()
            }
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }
}
