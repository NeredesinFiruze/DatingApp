package com.example.datingapp.data.local

import com.example.datingapp.util.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend fun updateSignInResult(updateSignIn: Boolean){
        userPreferences.updateSignIn(updateSignIn)
    }
    fun readSignIn(): Flow<Boolean?> = userPreferences.signInFlow
}