package com.mangoapps.phonebook.feature.contacts.data.remote

import com.mangoapps.phonebook.feature.contacts.data.remote.model.RemoteContactsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ContactsApi {
    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): RemoteContactsResponse
}
