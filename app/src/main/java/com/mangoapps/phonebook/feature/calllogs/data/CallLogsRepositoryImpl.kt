package com.mangoapps.phonebook.feature.calllogs.data

import com.mangoapps.phonebook.feature.calllogs.domain.model.CallLog
import com.mangoapps.phonebook.feature.calllogs.domain.repository.CallLogsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CallLogsRepositoryImpl @Inject constructor(
    private val localDataSource: CallLogsLocalDataSource
) : CallLogsRepository {
    override fun getIncomingCalls(): Flow<List<CallLog>> = flow { emit(localDataSource.getIncoming()) }
    override fun getOutgoingCalls(): Flow<List<CallLog>> = flow { emit(localDataSource.getOutgoing()) }
    override fun getMissedCalls(): Flow<List<CallLog>> = flow { emit(localDataSource.getMissed()) }
}
