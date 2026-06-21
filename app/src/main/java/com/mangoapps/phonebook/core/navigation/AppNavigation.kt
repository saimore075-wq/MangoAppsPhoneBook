package com.mangoapps.phonebook.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mangoapps.phonebook.core.datastore.AppPreferences
import com.mangoapps.phonebook.core.ui.components.AppDrawer
import com.mangoapps.phonebook.feature.calllogs.presentation.CallLogsScreen
import com.mangoapps.phonebook.feature.contacts.presentation.ContactsScreen
import com.mangoapps.phonebook.feature.sms.presentation.SmsScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    startDestination: String,
    appPreferences: AppPreferences
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                drawerState = drawerState,
                onNavigate = { screen ->
                    scope.launch { appPreferences.saveLastScreen(screen.route) }
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
            },
            exitTransition = {
                fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 }
            },
            popEnterTransition = {
                fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 }
            },
            popExitTransition = {
                fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 }
            }
        ) {
            composable(Screen.Contacts.route) {
                ContactsScreen(drawerState = drawerState)
            }
            composable(Screen.CallLogs.route) {
                CallLogsScreen(drawerState = drawerState)
            }
            composable(Screen.Sms.route) {
                SmsScreen(drawerState = drawerState)
            }
        }
    }
}
