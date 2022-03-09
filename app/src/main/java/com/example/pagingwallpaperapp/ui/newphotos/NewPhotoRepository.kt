package com.example.pagingwallpaperapp.ui.newphotos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.pagingwallpaperapp.api.UnsplashApi
import com.example.pagingwallpaperapp.data.source.ActionType
import com.example.pagingwallpaperapp.data.source.UnsplashPagingSource
import com.example.pagingwallpaperapp.util.PreferenceManager
import javax.inject.Inject


class NewPhotoRepository @Inject constructor(
  private val unsplashApi: UnsplashApi,
  private val preferenceManager: PreferenceManager
) {

  val preferences = preferenceManager

  fun getListOfPhoto(orderBy: String) = Pager(
    config = PagingConfig(
      pageSize = 20,
      maxSize = 100,
      enablePlaceholders = false
    ),
    pagingSourceFactory = {
      UnsplashPagingSource(unsplashApi, ActionType.BY_NEW_PHOTO_LIST_ORDER).apply {
        setOrder(orderBy)
      }
    }
  ).liveData
}