package com.mangoapps.phonebook.feature.sms.presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.core.ui.components.AppScaffold
import com.mangoapps.phonebook.core.ui.components.EmptyState
import com.mangoapps.phonebook.core.ui.components.ShimmerList

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SmsScreen(
    drawerState: DrawerState,
    viewModel: SmsViewModel = hiltViewModel()
) {
    val smsState by viewModel.smsState.collectAsStateWithLifecycle()
    val selectedSms by viewModel.selectedSms.collectAsStateWithLifecycle()
    val smsPermission = rememberPermissionState(Manifest.permission.READ_SMS)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!smsPermission.status.isGranted) {
            smsPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(smsPermission.status.isGranted) {
        if (smsPermission.status.isGranted) viewModel.loadSms()
    }

    AppScaffold(
        title = stringResource(R.string.nav_sms),
        drawerState = drawerState,
        onRefresh = { viewModel.refresh() }
    ) { paddingValues ->
        if (!smsPermission.status.isGranted) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    if (smsPermission.status.shouldShowRationale) {
                        Text(stringResource(R.string.permission_sms_rationale))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { smsPermission.launchPermissionRequest() }) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    } else {
                        Text(stringResource(R.string.permission_denied_permanently))
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null))
                            )
                        }) { Text(stringResource(R.string.open_settings)) }
                    }
                }
            }
            return@AppScaffold
        }

        val isLoading = smsState is SmsUiState.Loading
        SwipeRefresh(
            state = rememberSwipeRefreshState(isLoading),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(paddingValues)
        ) {
            when (smsState) {
                is SmsUiState.Loading -> ShimmerList()
                is SmsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((smsState as SmsUiState.Error).message)
                }
                is SmsUiState.Success -> {
                    val messages = (smsState as SmsUiState.Success).messages
                    if (messages.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Message,
                            title = stringResource(R.string.no_sms),
                            subtitle = stringResource(R.string.no_sms_subtitle)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                            )
                        ) {
                            items(messages, key = { it.id }) { sms ->
                                SmsItem(sms = sms, onClick = { viewModel.selectSms(sms) })
                            }
                        }
                    }
                }
            }
        }

        selectedSms?.let { sms ->
            SmsDetailDialog(sms = sms, onDismiss = { viewModel.dismissSms() })
        }
    }
}
