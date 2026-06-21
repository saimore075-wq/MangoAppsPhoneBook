package com.mangoapps.phonebook.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.core.navigation.Screen
import kotlinx.coroutines.launch

data class DrawerItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppDrawer(
    currentRoute: String,
    drawerState: DrawerState,
    onNavigate: (Screen) -> Unit
) {
    val scope = rememberCoroutineScope()
    val items = listOf(
        DrawerItem(Screen.Contacts, stringResource(R.string.nav_contacts), Icons.Default.Contacts),
        DrawerItem(Screen.CallLogs, stringResource(R.string.nav_call_logs), Icons.Default.Call),
        DrawerItem(Screen.Sms, stringResource(R.string.nav_sms), Icons.Default.Message)
    )

    ModalDrawerSheet {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    scope.launch { drawerState.close() }
                    onNavigate(item.screen)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
