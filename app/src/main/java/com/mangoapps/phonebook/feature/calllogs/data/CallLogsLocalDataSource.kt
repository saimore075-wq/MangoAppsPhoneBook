package com.mangoapps.phonebook.feature.calllogs.data

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.CallLog as AndroidCallLog
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import com.mangoapps.phonebook.core.util.Constants.EMPTY_STRING
import com.mangoapps.phonebook.feature.calllogs.domain.model.CallLog
import com.mangoapps.phonebook.feature.calllogs.domain.model.CallType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallLogsLocalDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext private val context: Context
) {
    private val simLabelCache = mutableMapOf<String, String?>()

    private fun resolveSimLabel(accountId: String?): String? {
        if (accountId.isNullOrBlank()) return null
        simLabelCache[accountId]?.let { return it }

        val label = try {
            val subId = accountId.trim().toIntOrNull()
            if (subId != null &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val sm = ContextCompat.getSystemService(context, SubscriptionManager::class.java)
                val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    sm?.getActiveSubscriptionInfo(subId)
                } else null
                info?.displayName?.toString() ?: "SIM $subId"
            } else if (subId != null) {
                "SIM $subId"
            } else {
                null
            }
        } catch (e: SecurityException) {
            val subId = accountId.trim().toIntOrNull()
            if (subId != null) "SIM $subId" else null
        } catch (e: Exception) {
            null
        }

        simLabelCache[accountId] = label
        return label
    }

    private suspend fun queryLogs(typeFilter: Int? = null): List<CallLog> = withContext(Dispatchers.IO) {
        val logs = mutableListOf<CallLog>()
        val projection = arrayOf(
            AndroidCallLog.Calls._ID,
            AndroidCallLog.Calls.NUMBER,
            AndroidCallLog.Calls.CACHED_NAME,
            AndroidCallLog.Calls.DURATION,
            AndroidCallLog.Calls.DATE,
            AndroidCallLog.Calls.TYPE,
            AndroidCallLog.Calls.PHONE_ACCOUNT_ID
        )
        val selection = typeFilter?.let { "${AndroidCallLog.Calls.TYPE} = ?" }
        val selectionArgs = typeFilter?.let { arrayOf(it.toString()) }
        contentResolver.query(
            AndroidCallLog.Calls.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${AndroidCallLog.Calls.DATE} DESC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(AndroidCallLog.Calls._ID)
            val numIdx = cursor.getColumnIndexOrThrow(AndroidCallLog.Calls.NUMBER)
            val nameIdx = cursor.getColumnIndexOrThrow(AndroidCallLog.Calls.CACHED_NAME)
            val durIdx = cursor.getColumnIndexOrThrow(AndroidCallLog.Calls.DURATION)
            val dateIdx = cursor.getColumnIndexOrThrow(AndroidCallLog.Calls.DATE)
            val typeIdx = cursor.getColumnIndexOrThrow(AndroidCallLog.Calls.TYPE)
            val accountIdx = cursor.getColumnIndex(AndroidCallLog.Calls.PHONE_ACCOUNT_ID)
            var count = 0
            while (cursor.moveToNext() && count < 200) {
                val rawType = cursor.getInt(typeIdx)
                val callType = when (rawType) {
                    1 -> CallType.INCOMING
                    2 -> CallType.OUTGOING
                    3 -> CallType.MISSED
                    else -> CallType.INCOMING
                }
                val accountId = if (accountIdx >= 0) cursor.getString(accountIdx) else null
                logs.add(
                    CallLog(
                        id = cursor.getLong(idIdx),
                        number = cursor.getString(numIdx) ?: EMPTY_STRING,
                        name = cursor.getString(nameIdx),
                        duration = cursor.getLong(durIdx),
                        date = cursor.getLong(dateIdx),
                        type = callType,
                        simLabel = resolveSimLabel(accountId)
                    )
                )
                count++
            }
        }
        logs
    }

    suspend fun getIncoming(): List<CallLog> = queryLogs(1)
    suspend fun getOutgoing(): List<CallLog> = queryLogs(2)
    suspend fun getMissed(): List<CallLog> = queryLogs(3)
}
