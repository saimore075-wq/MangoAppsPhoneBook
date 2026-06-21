package com.mangoapps.phonebook.feature.contacts.data.remote.model

data class RemoteContactData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val image: String
)

data class RemoteContactsResponse(
    val users: List<RemoteContactData>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
