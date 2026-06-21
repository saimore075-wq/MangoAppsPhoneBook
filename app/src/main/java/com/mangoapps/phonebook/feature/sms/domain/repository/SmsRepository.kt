package com.mangoapps.phonebook.feature.sms.domain.repository

import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import kotlinx.coroutines.flow.Flow

interface SmsRepository {
    fun getInboxMessages(): Flow<List<SmsMessage>>
}
