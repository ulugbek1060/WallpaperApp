package com.example.pagingwallpaperapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pagingwallpaperapp.data.querydata.QueryData

private const val APP_DATA_VERSION = 1
const val APP_DATA_NAME = "app_database"

@Database(entities = [QueryData::class], version = APP_DATA_VERSION)
abstract class AppDatabase : RoomDatabase() {

  abstract fun getQueryDao(): QueryDao

}