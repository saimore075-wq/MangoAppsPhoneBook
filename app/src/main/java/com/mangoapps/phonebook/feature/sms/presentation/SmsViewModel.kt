package com.mangoapps.phonebook.feature.sms.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import com.mangoapps.phonebook.feature.sms.domain.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SmsUiState {
    object Loading : SmsUiState()
    data class Success(val messages: List<SmsMessage>) : SmsUiState()
    data class Error(val message: String) : SmsUiState()
}

@HiltViewModel
class SmsViewModel @Inject constructor(
    private val repository: SmsRepository
) : ViewModel() {

    private val _smsState = MutableStateFlow<SmsUiState>(SmsUiState.Loading)
    val smsState: StateFlow<SmsUiState> = _smsState.asStateFlow()

    private val _selectedSms = MutableStateFlow<SmsMessage?>(null)
    val selectedSms: StateFlow<SmsMessage?> = _selectedSms.asStateFlow()

    private var observerJob: Job? = null

    init { loadSms() }

    fun loadSms() {
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
            _smsState.value = SmsUiState.Loading
            try {
                repository.getInboxMessages().collect { messages ->
                    _smsState.value = SmsUiState.Success(messages)
                }
            } catch (e: Exception) {
                _smsState.value = SmsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun refresh() = loadSms()
    fun selectSms(sms: SmsMessage) { _selectedSms.value = sms }
    fun dismissSms() { _selectedSms.value = null }
}
