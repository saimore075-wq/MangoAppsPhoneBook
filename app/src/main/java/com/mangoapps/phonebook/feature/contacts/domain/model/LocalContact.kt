package com.mangoapps.phonebook.feature.contacts.domain.model

data class LocalContact(
    val id: Long,
    val name: String,
    val phoneNumbers: List<String>,
    val photoUri: String?
)
