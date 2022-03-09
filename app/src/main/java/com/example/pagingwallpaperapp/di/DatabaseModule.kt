package com.example.pagingwallpaperapp.di

import android.app.Application
import androidx.room.Room
import com.example.pagingwallpaperapp.db.APP_DATA_NAME
import com.example.pagingwallpaperapp.db.AppDatabase
import com.example.pagingwallpaperapp.db.QueryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideAppDatabase(
    app: Application
  ): AppDatabase {
    return Room.databaseBuilder(app, AppDatabase::class.java, APP_DATA_NAME)
      .fallbackToDestructiveMigration()
      .build()
  }

  @Provides
  @Singleton
  fun provideQueryDao(appDatabase: AppDatabase): QueryDao {
    return appDatabase.getQueryDao()
  }
}