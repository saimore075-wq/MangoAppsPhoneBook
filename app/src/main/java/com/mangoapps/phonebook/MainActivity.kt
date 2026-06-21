package com.mangoapps.phonebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.mangoapps.phonebook.core.datastore.AppPreferences
import com.mangoapps.phonebook.core.navigation.AppNavigation
import com.mangoapps.phonebook.core.ui.theme.MangoAppsPhoneBookTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MangoAppsPhoneBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startRoute by appPreferences.getLastScreen()
                        .collectAsState(initial = "contacts")
                    AppNavigation(
                        startDestination = startRoute,
                        appPreferences = appPreferences
                    )
                }
            }
        }
    }
}
