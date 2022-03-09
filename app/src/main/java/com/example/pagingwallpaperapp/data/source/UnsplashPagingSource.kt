package com.example.pagingwallpaperapp.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pagingwallpaperapp.api.UnsplashApi
import com.example.pagingwallpaperapp.api.UnsplashApi.Companion.POPULAR
import com.example.pagingwallpaperapp.data.UnsplashPhoto
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_INDEX = 1

enum class ActionType { BY_SEARCH_ORDER, BY_NEW_PHOTO_LIST_ORDER }

class UnsplashPagingSource(
  private val unsplashApi: UnsplashApi,
  private val actionType: ActionType
) : PagingSource<Int, UnsplashPhoto>() {
  override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
    return state.anchorPosition
  }

  private var query: String? = null
  private var orderBy: String = POPULAR

  fun setQuery(typed: String) {
    query = typed
  }

  fun setOrder(typed: String) {
    orderBy = typed
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
    val position = params.key ?: STARTING_INDEX
    return try {
      val list =
        getResponseByType(actionType, query, unsplashApi, position, params.loadSize, orderBy)
      LoadResult.Page(
        data = list,
        prevKey = if (position == STARTING_INDEX) null else position - 1,
        nextKey = if (list.isEmpty()) null else position + 1
      )
    } catch (exception: IOException) {
      LoadResult.Error(exception)
    } catch (exception: HttpException) {
      LoadResult.Error(exception)
    }
  }

  private suspend fun getResponseByType(
    actionType: ActionType,
    query: String?,
    unsplashApi: UnsplashApi,
    position: Int,
    perPage: Int,
    orderBy: String
  ): List<UnsplashPhoto> {
    when (actionType) {
      ActionType.BY_NEW_PHOTO_LIST_ORDER -> {
        val response = unsplashApi.newPhotosList(position, perPage, orderBy)
        return if (response.isSuccessful) {
          if (response.body() != null) {
            response.body()!!
          } else {
            emptyList()
          }
        } else {
          emptyList()
        }
      }
      ActionType.BY_SEARCH_ORDER -> {
        val response = unsplashApi.searchPhotos(query!!, position, perPage)
        return if (response.isSuccessful) {
          if (response.body() != null) {
            response.body()!!.results
          } else {
            emptyList()
          }
        } else {
          emptyList()
        }
      }
      else -> {
        return emptyList()
      }
    }
  }
}