package com.example.pagingwallpaperapp.ui.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.pagingwallpaperapp.api.UnsplashApi
import com.example.pagingwallpaperapp.data.source.ActionType
import com.example.pagingwallpaperapp.data.source.UnsplashPagingSource
import com.example.pagingwallpaperapp.db.QueryDao
import javax.inject.Inject

class SearchRepository @Inject constructor(
  private val unsplashApi: UnsplashApi,
  private val dao: QueryDao
) {

  val queryDao = dao

  fun getSearchResult(query: String) = Pager(
    config = PagingConfig(
      pageSize = 20,
      maxSize = 100,
      enablePlaceholders = false
    ),
    pagingSourceFactory = {
      UnsplashPagingSource(unsplashApi, ActionType.BY_SEARCH_ORDER).apply {
        setQuery(query)
      }
    }
  ).liveData
}