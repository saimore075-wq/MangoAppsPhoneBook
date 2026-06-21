package com.mangoapps.phonebook.feature.calllogs.domain.repository

import com.mangoapps.phonebook.feature.calllogs.domain.model.CallLog
import kotlinx.coroutines.flow.Flow

interface CallLogsRepository {
    fun getIncomingCalls(): Flow<List<CallLog>>
    fun getOutgoingCalls(): Flow<List<CallLog>>
    fun getMissedCalls(): Flow<List<CallLog>>
}
