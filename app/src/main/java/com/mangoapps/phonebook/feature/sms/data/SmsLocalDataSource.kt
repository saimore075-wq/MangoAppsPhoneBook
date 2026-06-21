package com.mangoapps.phonebook.feature.sms.data

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import com.mangoapps.phonebook.core.util.Constants.EMPTY_STRING
import com.mangoapps.phonebook.feature.sms.domain.model.SmsMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SmsLocalDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun getInboxMessages(): List<SmsMessage> = withContext(Dispatchers.IO) {
        val messages = mutableListOf<SmsMessage>()
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )
        contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
            val addrIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
            var count = 0
            while (cursor.moveToNext() && count < 100) {
                messages.add(
                    SmsMessage(
                        id = cursor.getLong(idIdx),
                        sender = cursor.getString(addrIdx) ?: EMPTY_STRING,
                        body = cursor.getString(bodyIdx) ?: EMPTY_STRING,
                        date = cursor.getLong(dateIdx)
                    )
                )
                count++
            }
        }
        messages
    }

    @Suppress("OPT_IN_USAGE")
    fun observeInboxMessages(): Flow<List<SmsMessage>> = callbackFlow {
        trySend(Unit)
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { trySend(Unit) }
            override fun onChange(selfChange: Boolean, uri: Uri?) { trySend(Unit) }
        }
        contentResolver.registerContentObserver(Telephony.Sms.CONTENT_URI, true, observer)
        contentResolver.registerContentObserver(Telephony.MmsSms.CONTENT_URI, true, observer)
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.flatMapLatest {
        flow { emit(getInboxMessages()) }
    }
}
