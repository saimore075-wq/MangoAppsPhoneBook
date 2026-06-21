package com.mangoapps.phonebook.feature.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mangoapps.phonebook.R
import com.mangoapps.phonebook.core.ui.components.EmptyState
import com.mangoapps.phonebook.core.ui.components.ShimmerList
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact

@Composable
fun InitialsAvatar(name: String, modifier: Modifier = Modifier) {
    val colors = listOf(
        Color(0xFF1565C0), Color(0xFF00ACC1), Color(0xFF6A1B9A),
        Color(0xFF2E7D32), Color(0xFFE65100), Color(0xFF4527A0)
    )
    val color = colors[kotlin.math.abs(name.hashCode()) % colors.size]
    val initial = name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun LocalContactsTab(
    state: ContactsUiState,
    onRefresh: () -> Unit,
    paddingValues: PaddingValues
) {
    var selectedContact by remember { mutableStateOf<LocalContact?>(null) }
    val isRefreshing = state is ContactsUiState.Loading
    val swipeState = rememberSwipeRefreshState(isRefreshing)

    SwipeRefresh(state = swipeState, onRefresh = onRefresh) {
        when (state) {
            is ContactsUiState.Loading -> ShimmerList()
            is ContactsUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
            is ContactsUiState.Success -> {
                if (state.contacts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Contacts,
                        title = stringResource(R.string.no_contacts),
                        subtitle = stringResource(R.string.no_contacts_subtitle)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        )
                    ) {
                        items(state.contacts, key = { it.id }) { contact ->
                            ContactCard(contact = contact, onClick = {
                                selectedContact = contact
                            })
                        }
                    }
                }
            }
        }
    }

    selectedContact?.let { contact ->
        ContactActionDialog(contact = contact, onDismiss = { selectedContact = null })
    }
}

@Composable
private fun ContactCard(contact: LocalContact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = contact.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                )
            } else {
                InitialsAvatar(name = contact.name, modifier = Modifier.size(52.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                val numberLabel = if (contact.phoneNumbers.size == 1) {
                    contact.phoneNumbers.first()
                } else {
                    "${contact.phoneNumbers.first()} (+${contact.phoneNumbers.size - 1} more)"
                }
                Text(
                    text = numberLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
