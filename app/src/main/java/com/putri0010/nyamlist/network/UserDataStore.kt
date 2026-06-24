package com.putri0010.nyamlist.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.putri0010.nyamlist.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
    name = "user_preference"
)

class UserDataStore(private val context: Context){

    companion object {
        private val USER_NAME = stringPreferencesKey("nama")
        private val USER_EMAIL = stringPreferencesKey("email")
        private val USER_PHOTO = stringPreferencesKey("photoUrl")
        private val USER_TOKEN = stringPreferencesKey("idToken")
        private val LAYOUT_KEY = booleanPreferencesKey("layout_setting") // Kunci untuk grid/list
    }
    val userFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            nama = preferences[USER_NAME] ?: "",
            email = preferences[USER_EMAIL] ?: "",
            photoUrl = preferences[USER_PHOTO] ?: "",
            idToken = preferences[USER_TOKEN] ?: ""
        )
    }

    val layoutFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LAYOUT_KEY] ?: true
    }

    suspend fun saveData(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = user.nama
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHOTO] = user.photoUrl
            preferences[USER_TOKEN] = user.idToken
        }
    }

    suspend fun saveLayout(isList: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LAYOUT_KEY] = isList
        }
    }
}