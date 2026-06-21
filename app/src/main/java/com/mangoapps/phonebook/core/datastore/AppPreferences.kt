package com.mangoapps.phonebook.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LAST_SCREEN_KEY = stringPreferencesKey("last_screen")
    }

    fun getLastScreen(): Flow<String> = dataStore.data.map { prefs ->
        prefs[LAST_SCREEN_KEY] ?: "contacts"
    }

    suspend fun saveLastScreen(route: String) {
        dataStore.edit { prefs ->
            prefs[LAST_SCREEN_KEY] = route
        }
    }
}
