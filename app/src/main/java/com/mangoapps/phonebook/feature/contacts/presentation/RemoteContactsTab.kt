package com.mangoapps.phonebook.feature.contacts.presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.core.ui.components.ErrorState
import com.mangoapps.phonebook.core.ui.components.LoadingState
import com.mangoapps.phonebook.feature.contacts.domain.model.RemoteContact

@Composable
fun RemoteContactsTab(
    pagingItems: LazyPagingItems<RemoteContact>,
    paddingValues: PaddingValues
) {
    var selectedContact by remember { mutableStateOf<RemoteContact?>(null) }

    val refreshState = pagingItems.loadState.refresh
    val hasItems = pagingItems.itemCount > 0

    when {
        refreshState is LoadState.Loading && !hasItems -> LoadingState()
        refreshState is LoadState.Error && !hasItems -> ErrorState(
            message = "Connection lost. Please check the network and try again.",
            onRetry = { pagingItems.refresh() }
        )
        else -> {
            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = rememberSwipeRefreshState(isRefreshing = refreshState is LoadState.Loading),
                onRefresh = { pagingItems.refresh() }
            ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        )
                    ) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.id }
                        ) { index ->
                            val contact = pagingItems[index]
                            if (contact != null) {
                                RemoteContactCard(contact = contact, onClick = { selectedContact = contact })
                            }
                        }
                        item {
                            when (val append = pagingItems.loadState.append) {
                                is LoadState.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                    }
                                }
                                is LoadState.Error -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TextButton(onClick = { pagingItems.retry() }) {
                                            Text("Retry")
                                        }
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

    selectedContact?.let { contact ->
        RemoteContactActionDialog(
            contact = contact,
            onDismiss = { selectedContact = null }
        )
    }
}

@Composable
private fun RemoteContactCard(contact: RemoteContact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = contact.imageUrl,
                contentDescription = contact.fullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(52.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.foundation.layout.Column {
                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = contact.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RemoteContactActionDialog(
    contact: RemoteContact,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var pendingCallNumber by remember { mutableStateOf<String?>(null) }
    var callPermissionRequested by remember { mutableStateOf(false) }

    val callPermission = rememberPermissionState(
        permission = Manifest.permission.CALL_PHONE,
        onPermissionResult = { granted ->
            val number = pendingCallNumber ?: return@rememberPermissionState
            pendingCallNumber = null
            if (granted) {
                context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
            } else {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
            }
            onDismiss()
        }
    )

    fun makeCall(number: String) {
        when {
            callPermission.status.isGranted -> {
                context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
                onDismiss()
            }
            !callPermission.status.shouldShowRationale && callPermissionRequested -> {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
                onDismiss()
            }
            else -> {
                pendingCallNumber = number
                callPermissionRequested = true
                callPermission.launchPermissionRequest()
            }
        }
    }

    fun sendSms(number: String) {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")))
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.action),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider()
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = contact.imageUrl,
                        contentDescription = contact.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(56.dp).clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = contact.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = contact.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { sendSms(contact.phone) }) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.sms))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { makeCall(contact.phone) }) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.call))
                    }
                }
            }
        }
    }
}
