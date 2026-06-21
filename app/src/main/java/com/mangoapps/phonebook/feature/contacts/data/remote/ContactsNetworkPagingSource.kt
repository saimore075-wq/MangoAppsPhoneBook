package com.mangoapps.phonebook.feature.contacts.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mangoapps.phonebook.feature.contacts.data.local.db.RemoteContactDao
import com.mangoapps.phonebook.feature.contacts.data.local.db.RemoteContactEntity
import com.mangoapps.phonebook.feature.contacts.domain.model.RemoteContact

class ContactsNetworkPagingSource(
    private val api: ContactsApi,
    private val dao: RemoteContactDao
) : PagingSource<Int, RemoteContact>() {

    companion object {
        const val PAGE_SIZE = 30
    }

    override fun getRefreshKey(state: PagingState<Int, RemoteContact>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RemoteContact> {
        val page = params.key ?: 0
        return try {
            val response = api.getUsers(limit = PAGE_SIZE, skip = page * PAGE_SIZE)
            val contacts = response.users.map {
                RemoteContact(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                    phone = it.phone,
                    imageUrl = it.image
                )
            }
            if (page == 0) dao.clearAll()
            dao.insertAll(response.users.map {
                RemoteContactEntity(it.id, it.firstName, it.lastName, it.email, it.phone, it.image)
            })
            val endOfPagination = response.users.isEmpty() ||
                response.skip + response.users.size >= response.total
            LoadResult.Page(
                data = contacts,
                prevKey = null,
                nextKey = if (endOfPagination) null else page + 1
            )
        } catch (e: Exception) {
            if (page == 0) {
                val cached = dao.getAllAsList()
                if (cached.isNotEmpty()) {
                    return LoadResult.Page(
                        data = cached.map { it.toRemoteContact() },
                        prevKey = null,
                        nextKey = null
                    )
                }
            }
            LoadResult.Error(e)
        }
    }
}
