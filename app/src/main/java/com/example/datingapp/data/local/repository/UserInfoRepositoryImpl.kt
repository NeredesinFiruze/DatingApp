package com.example.datingapp.data.local.repository

import com.example.datingapp.data.local.UserInfoDAO
import com.example.datingapp.data.local.UserInfoEntity

class UserInfoRepositoryImpl(private val dao: UserInfoDAO): UserInfoRepository {

    override suspend fun updateInfo(userInfo: UserInfoEntity) = dao.updateInfo(userInfo)

    override fun isNameNotEmpty(): Boolean = dao.isNameNotEmpty()

    override suspend fun getUserInfo(): List<UserInfoEntity> = dao.getUserInfo()

    override suspend fun deleteInfo() = dao.deleteInfo()
}