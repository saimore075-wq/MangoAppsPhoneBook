package com.mangoapps.phonebook.feature.contacts.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.core.ui.components.AppScaffold
import android.Manifest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsScreen(
    drawerState: DrawerState,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val localState by viewModel.localContactsState.collectAsStateWithLifecycle()
    val remoteContacts = viewModel.remoteContacts.collectAsLazyPagingItems()
    var selectedTab by remember { mutableIntStateOf(0) }
    val contactsPermission = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    var permissionRequested by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!contactsPermission.status.isGranted) {
            permissionRequested = true
            contactsPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(contactsPermission.status.isGranted) {
        if (contactsPermission.status.isGranted) {
            viewModel.loadLocalContacts()
        }
    }

    AppScaffold(
        title = stringResource(R.string.nav_contacts),
        drawerState = drawerState,
        onRefresh = { viewModel.refresh() }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.tab_local)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.tab_remote)) }
                )
            }
            when (selectedTab) {
                0 -> {
                    when {
                        contactsPermission.status.isGranted -> {
                            LocalContactsTab(
                                state = localState,
                                onRefresh = { viewModel.refresh() },
                                paddingValues = paddingValues
                            )
                        }
                        contactsPermission.status.shouldShowRationale || !permissionRequested -> {
                            PermissionRationaleCard(
                                message = stringResource(R.string.permission_contacts_rationale),
                                onGrant = {
                                    permissionRequested = true
                                    contactsPermission.launchPermissionRequest()
                                }
                            )
                        }
                        else -> {
                            PermissionDeniedCard(
                                onOpenSettings = {
                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                1 -> RemoteContactsTab(pagingItems = remoteContacts, paddingValues = paddingValues)
            }
        }
    }
}

@Composable
private fun PermissionDeniedCard(onOpenSettings: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text(stringResource(R.string.permission_denied_permanently))
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onOpenSettings) { Text(stringResource(R.string.open_settings)) }
        }
    }
}

@Composable
private fun PermissionRationaleCard(message: String, onGrant: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text(message)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onGrant) { Text(stringResource(R.string.grant_permission)) }
        }
    }
}

