package com.example.pagingwallpaperapp.data.querydata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "query")
data class QueryData(
  @PrimaryKey
  val text: String
)