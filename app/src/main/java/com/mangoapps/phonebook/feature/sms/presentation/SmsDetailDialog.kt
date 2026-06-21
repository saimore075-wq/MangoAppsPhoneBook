package com.mangoapps.phonebook.feature.sms.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SmsDetailDialog(sms: SmsMessage, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(sms.sender, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(stringResource(R.string.message_label), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(sms.body, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.time_label), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(dateFormat.format(Date(sms.date)), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
        }
    )
}
