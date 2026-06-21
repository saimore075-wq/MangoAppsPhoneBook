package com.mangoapps.phonebook.feature.contacts.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mangoapps.phonebook.feature.contacts.domain.model.RemoteContact

@Entity(tableName = "remote_contacts")
data class RemoteContactEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val imageUrl: String
) {
    fun toRemoteContact() = RemoteContact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        imageUrl = imageUrl
    )
}
