package com.example.rentmycar.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// DataStore instance
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object JwtDataStore {
    private val JWT_KEY = stringPreferencesKey("jwt_token")

    // Save the token
    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_KEY] = token
        }
    }

    // Get the token
    private suspend fun getToken(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[JWT_KEY]
    }

    // Check if the token exists
    fun hasToken(context: Context): Boolean {
        return runBlocking {
            getToken(context)?.isNotEmpty() == true
        }
    }

    // Clear the token
    suspend fun clearToken(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_KEY)
        }
    }
}
