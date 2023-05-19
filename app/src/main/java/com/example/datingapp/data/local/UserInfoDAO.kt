package com.example.datingapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInfoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInfo(userInfo: UserInfoEntity)

    @Query("SELECT NOT EXISTS(SELECT 1 FROM userInfo WHERE name = '' LIMIT 1)")
    fun isNameNotEmpty(): Boolean

    @Query("SELECT * FROM userInfo LIMIT 1")
    suspend fun getUserInfo(): List<UserInfoEntity>

    @Query("DELETE FROM userInfo WHERE id = 1")
    suspend fun deleteInfo()
}