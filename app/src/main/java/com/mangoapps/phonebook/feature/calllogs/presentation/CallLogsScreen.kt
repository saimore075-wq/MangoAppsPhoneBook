package com.mangoapps.phonebook.feature.calllogs.presentation

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.mangoapps.phonebook.feature.calllogs.domain.model.CallLog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallLogsScreen(
    drawerState: DrawerState,
    viewModel: CallLogsViewModel = hiltViewModel()
) {
    val incomingState by viewModel.incomingState.collectAsStateWithLifecycle()
    val outgoingState by viewModel.outgoingState.collectAsStateWithLifecycle()
    val missedState by viewModel.missedState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val callLogPermission = rememberPermissionState(Manifest.permission.READ_CALL_LOG)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!callLogPermission.status.isGranted) {
            callLogPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(callLogPermission.status.isGranted) {
        if (callLogPermission.status.isGranted) viewModel.loadAll()
    }

    fun countOf(state: CallLogsUiState) = if (state is CallLogsUiState.Success) state.logs.size else 0

    AppScaffold(
        title = stringResource(R.string.nav_call_logs),
        drawerState = drawerState,
        onRefresh = { viewModel.refresh() }
    ) { paddingValues ->
        if (!callLogPermission.status.isGranted) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    if (callLogPermission.status.shouldShowRationale) {
                        Text(stringResource(R.string.permission_call_log_rationale))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { callLogPermission.launchPermissionRequest() }) {
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

        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                listOf(
                    stringResource(R.string.tab_incoming) to countOf(incomingState),
                    stringResource(R.string.tab_outgoing) to countOf(outgoingState),
                    stringResource(R.string.tab_missed) to countOf(missedState)
                ).forEachIndexed { index, (label, count) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            BadgedBox(badge = {
                                if (count > 0) Badge { Text(if (count > 99) "99+" else count.toString()) }
                            }) { Text(label) }
                        }
                    )
                }
            }

            val currentState = when (selectedTab) {
                0 -> incomingState
                1 -> outgoingState
                else -> missedState
            }
            val isLoading = currentState is CallLogsUiState.Loading
            SwipeRefresh(state = rememberSwipeRefreshState(isLoading), onRefresh = { viewModel.refresh() }) {
                when (currentState) {
                    is CallLogsUiState.Loading -> Box(Modifier.fillMaxSize())
                    is CallLogsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(currentState.message)
                    }
                    is CallLogsUiState.Success -> {
                        if (currentState.logs.isEmpty()) {
                            EmptyState(
                                icon = Icons.Default.Call,
                                title = stringResource(R.string.no_call_logs),
                                subtitle = stringResource(R.string.no_call_logs_subtitle)
                            )
                        } else {
                            CallLogsList(logs = currentState.logs)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallLogsList(logs: List<CallLog>) {
    LazyColumn(
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        )
    ) {
        items(logs, key = { it.id }) { log ->
            CallLogItem(log = log)
        }
    }
}
