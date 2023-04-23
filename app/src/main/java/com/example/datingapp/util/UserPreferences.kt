package com.example.datingapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    private companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
        val COMPLETE_SIGN_IN =  booleanPreferencesKey("is_complete_sign_in")
    }

    val signInFlow: Flow<Boolean?> = context.dataStore.data.map {
        it[COMPLETE_SIGN_IN] ?: false
    }

    suspend fun updateSignIn(signInResult: Boolean){
        context.dataStore.edit {
            it[COMPLETE_SIGN_IN] = signInResult
        }
    }
}