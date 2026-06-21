package com.mangoapps.phonebook.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mangoapps.phonebook.feature.contacts.data.local.db.RemoteContactDao
import com.mangoapps.phonebook.feature.contacts.data.local.db.RemoteContactEntity

@Database(
    entities = [RemoteContactEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remoteContactDao(): RemoteContactDao
}
