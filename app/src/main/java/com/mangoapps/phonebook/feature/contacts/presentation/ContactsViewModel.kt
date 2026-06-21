package com.mangoapps.phonebook.feature.contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact
import com.mangoapps.phonebook.feature.contacts.domain.model.RemoteContact
import com.mangoapps.phonebook.feature.contacts.domain.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ContactsUiState {
    object Loading : ContactsUiState()
    data class Success(val contacts: List<LocalContact>) : ContactsUiState()
    data class Error(val message: String) : ContactsUiState()
}

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactsRepository
) : ViewModel() {

    private val _localContactsState = MutableStateFlow<ContactsUiState>(ContactsUiState.Loading)
    val localContactsState: StateFlow<ContactsUiState> = _localContactsState.asStateFlow()

    val remoteContacts: Flow<PagingData<RemoteContact>> = repository
        .getRemoteContacts()
        .cachedIn(viewModelScope)

    init {
        loadLocalContacts()
    }

    fun loadLocalContacts() {
        viewModelScope.launch {
            _localContactsState.value = ContactsUiState.Loading
            try {
                repository.getLocalContacts().collect { contacts ->
                    _localContactsState.value = ContactsUiState.Success(contacts)
                }
            } catch (e: SecurityException) {
                _localContactsState.value = ContactsUiState.Loading
            } catch (e: Exception) {
                _localContactsState.value = ContactsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun refresh() {
        loadLocalContacts()
    }
}
