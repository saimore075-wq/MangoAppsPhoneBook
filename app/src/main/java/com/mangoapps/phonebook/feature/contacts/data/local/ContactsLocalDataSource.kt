package com.mangoapps.phonebook.feature.contacts.data.local

import android.content.ContentResolver
import android.provider.ContactsContract
import com.mangoapps.phonebook.core.util.Constants.EMPTY_STRING
import com.mangoapps.phonebook.feature.contacts.domain.model.LocalContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsLocalDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun getContacts(): List<LocalContact> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        val numbersMap = linkedMapOf<Long, MutableList<String>>()
        val nameMap = linkedMapOf<Long, String>()
        val photoMap = linkedMapOf<Long, String?>()

        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIdx)
                val number = cursor.getString(numberIdx) ?: return@use
                if (!numbersMap.containsKey(id)) {
                    nameMap[id] = cursor.getString(nameIdx) ?: EMPTY_STRING
                    photoMap[id] = cursor.getString(photoIdx)
                    numbersMap[id] = mutableListOf()
                }
                numbersMap[id]!!.add(number)
            }
        }

        numbersMap.map { (id, numbers) ->
            LocalContact(
                id = id,
                name = nameMap[id] ?: EMPTY_STRING,
                phoneNumbers = numbers,
                photoUri = photoMap[id]
            )
        }
    }
}
