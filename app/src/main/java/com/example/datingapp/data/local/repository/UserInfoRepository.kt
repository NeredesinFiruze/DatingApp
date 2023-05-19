package com.example.datingapp.data.local.repository

import com.example.datingapp.data.local.UserInfoEntity

interface UserInfoRepository {

    suspend fun updateInfo(userInfo: UserInfoEntity)

    fun isNameNotEmpty(): Boolean

    suspend fun getUserInfo(): List<UserInfoEntity>

    suspend fun deleteInfo()
}