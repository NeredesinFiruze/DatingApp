package com.example.datingapp.di

import android.app.Application
import androidx.room.Room
import com.example.datingapp.data.local.UserInfoDatabase
import com.example.datingapp.data.local.repository.UserInfoRepository
import com.example.datingapp.data.local.repository.UserInfoRepositoryImpl
import com.example.datingapp.util.Constants.DATABASE_NAME
import com.example.datingapp.util.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDataStore(application: Application): UserPreferences {
        return UserPreferences(application.applicationContext)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application): UserInfoDatabase{
        return Room.databaseBuilder(
            application.applicationContext,
            UserInfoDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(database: UserInfoDatabase): UserInfoRepository{
        return UserInfoRepositoryImpl(database.dao)
    }
}