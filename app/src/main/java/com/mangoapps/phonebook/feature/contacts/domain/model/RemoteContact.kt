package com.mangoapps.phonebook.feature.contacts.domain.model

data class RemoteContact(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val imageUrl: String
) {
    val fullName: String get() = "$firstName $lastName"
}
