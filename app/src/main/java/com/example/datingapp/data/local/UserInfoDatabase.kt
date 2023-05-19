package com.example.datingapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.datingapp.util.Converters

@Database([UserInfoEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class UserInfoDatabase: RoomDatabase() {

    abstract val dao: UserInfoDAO
}