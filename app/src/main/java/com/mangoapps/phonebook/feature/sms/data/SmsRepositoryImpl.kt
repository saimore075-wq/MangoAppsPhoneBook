package com.mangoapps.phonebook.feature.sms.data

import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import com.mangoapps.phonebook.feature.sms.domain.repository.SmsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SmsRepositoryImpl @Inject constructor(
    private val localDataSource: SmsLocalDataSource
) : SmsRepository {
    override fun getInboxMessages(): Flow<List<SmsMessage>> = localDataSource.observeInboxMessages()
}
