package com.mangoapps.phonebook.feature.contacts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<RemoteContactEntity>)

    @Query("SELECT * FROM remote_contacts")
    suspend fun getAllAsList(): List<RemoteContactEntity>

    @Query("DELETE FROM remote_contacts")
    suspend fun clearAll()
}
