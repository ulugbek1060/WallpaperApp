package com.example.pagingwallpaperapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.pagingwallpaperapp.data.querydata.QueryData
import kotlinx.coroutines.flow.Flow


@Dao
interface QueryDao {

  @Insert(onConflict = REPLACE)
  suspend fun insert(queryData: QueryData)

  @Query("DELETE FROM `query`")
  suspend fun delete()

  @Query("SELECT * FROM `query`")
  fun getAllList(): Flow<List<QueryData>>

}