package com.mangoapps.phonebook.feature.contacts.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mangoapps.phonebook.core.db.AppDatabase
import com.mangoapps.phonebook.feature.contacts.data.local.ContactsLocalDataSource
import com.mangoapps.phonebook.feature.contacts.data.remote.ContactsApi
import com.mangoapps.phonebook.feature.contacts.data.remote.ContactsNetworkPagingSource
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact
import com.mangoapps.phonebook.feature.contacts.domain.model.RemoteContact
import com.mangoapps.phonebook.feature.contacts.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val localDataSource: ContactsLocalDataSource,
    private val api: ContactsApi,
    private val db: AppDatabase
) : ContactsRepository {

    override fun getLocalContacts(): Flow<List<LocalContact>> = flow {
        emit(localDataSource.getContacts())
    }

    override fun getRemoteContacts(): Flow<PagingData<RemoteContact>> =
        Pager(
            config = PagingConfig(
                pageSize = ContactsNetworkPagingSource.PAGE_SIZE,
                initialLoadSize = ContactsNetworkPagingSource.PAGE_SIZE,
                prefetchDistance = ContactsNetworkPagingSource.PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ContactsNetworkPagingSource(api, db.remoteContactDao()) }
        ).flow
}
