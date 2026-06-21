package com.mangoapps.phonebook.feature.calllogs.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangoapps.phonebook.feature.calllogs.domain.model.CallLog
import com.mangoapps.phonebook.feature.calllogs.domain.repository.CallLogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CallLogsUiState {
    object Loading : CallLogsUiState()
    data class Success(val logs: List<CallLog>) : CallLogsUiState()
    data class Error(val message: String) : CallLogsUiState()
}

@HiltViewModel
class CallLogsViewModel @Inject constructor(
    private val repository: CallLogsRepository
) : ViewModel() {

    private val _incomingState = MutableStateFlow<CallLogsUiState>(CallLogsUiState.Loading)
    val incomingState: StateFlow<CallLogsUiState> = _incomingState.asStateFlow()

    private val _outgoingState = MutableStateFlow<CallLogsUiState>(CallLogsUiState.Loading)
    val outgoingState: StateFlow<CallLogsUiState> = _outgoingState.asStateFlow()

    private val _missedState = MutableStateFlow<CallLogsUiState>(CallLogsUiState.Loading)
    val missedState: StateFlow<CallLogsUiState> = _missedState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _incomingState.value = CallLogsUiState.Loading
            _outgoingState.value = CallLogsUiState.Loading
            _missedState.value = CallLogsUiState.Loading
            try {
                val incoming = async {
                    repository.getIncomingCalls().catch { emit(emptyList()) }.first()
                }
                val outgoing = async {
                    repository.getOutgoingCalls().catch { emit(emptyList()) }.first()
                }
                val missed = async {
                    repository.getMissedCalls().catch { emit(emptyList()) }.first()
                }
                _incomingState.value = CallLogsUiState.Success(incoming.await())
                _outgoingState.value = CallLogsUiState.Success(outgoing.await())
                _missedState.value = CallLogsUiState.Success(missed.await())
            } catch (e: Exception) {
                val msg = e.message ?: "Unknown error"
                _incomingState.value = CallLogsUiState.Error(msg)
                _outgoingState.value = CallLogsUiState.Error(msg)
                _missedState.value = CallLogsUiState.Error(msg)
            }
        }
    }

    fun refresh() = loadAll()
}
