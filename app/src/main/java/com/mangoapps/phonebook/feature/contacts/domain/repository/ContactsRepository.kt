package com.mangoapps.phonebook.feature.contacts.domain.repository

import androidx.paging.PagingData
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact
import com.mangoapps.phonebook.feature.contacts.domain.model.RemoteContact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getLocalContacts(): Flow<List<LocalContact>>
    fun getRemoteContacts(): Flow<PagingData<RemoteContact>>
}
