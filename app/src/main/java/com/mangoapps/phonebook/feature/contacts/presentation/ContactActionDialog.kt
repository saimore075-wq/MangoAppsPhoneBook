package com.mangoapps.phonebook.feature.contacts.presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactActionDialog(
    contact: LocalContact,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var pendingCallNumber by remember { mutableStateOf<String?>(null) }
    var callPermissionRequested by remember { mutableStateOf(false) }
    var showNumberPicker by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<NumberAction?>(null) }

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
                // Permanently denied — skip dialog, go straight to dialer
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

    fun handleCall() {
        if (contact.phoneNumbers.size == 1) {
            makeCall(contact.phoneNumbers.first())
        } else {
            pendingAction = NumberAction.CALL
            showNumberPicker = true
        }
    }

    fun handleSms() {
        if (contact.phoneNumbers.size == 1) {
            sendSms(contact.phoneNumbers.first())
        } else {
            pendingAction = NumberAction.SMS
            showNumberPicker = true
        }
    }

    if (showNumberPicker) {
        NumberPickerDialog(
            contact = contact,
            action = pendingAction ?: NumberAction.CALL,
            onPick = { number ->
                showNumberPicker = false
                when (pendingAction) {
                    NumberAction.CALL -> makeCall(number)
                    NumberAction.SMS -> sendSms(number)
                    null -> {}
                }
            },
            onDismiss = { showNumberPicker = false }
        )
        return
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
                    if (contact.photoUri != null) {
                        AsyncImage(
                            model = contact.photoUri,
                            contentDescription = contact.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(56.dp).clip(CircleShape)
                        )
                    } else {
                        InitialsAvatar(name = contact.name, modifier = Modifier.size(56.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                contact.phoneNumbers.forEachIndexed { index, number ->
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
                            text = number,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (index < contact.phoneNumbers.size - 1) {
                        Divider(
                            modifier = Modifier.padding(start = 26.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
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
                    TextButton(onClick = { handleSms() }) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.sms))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { handleCall() }) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.call))
                    }
                }
            }
        }
    }
}

private enum class NumberAction { CALL, SMS }

@Composable
private fun NumberPickerDialog(
    contact: LocalContact,
    action: NumberAction,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val title = if (action == NumberAction.CALL) stringResource(R.string.select_number_to_call)
                else stringResource(R.string.select_number_to_sms)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                contact.phoneNumbers.forEach { number ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick(number) }
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (action == NumberAction.CALL) Icons.Default.Call else Icons.Default.Message,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(number, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
        }
    )
}
