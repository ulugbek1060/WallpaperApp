package com.example.pagingwallpaperapp.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val repository: SearchRepository,
  state: SavedStateHandle
) : ViewModel() {

  private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

  val photos = currentQuery.switchMap {
    repository.getSearchResult(it).cachedIn(viewModelScope)
  }

  fun searchPhoto(query: String) {
    currentQuery.value = query
  }

  companion object {
    private const val CURRENT_QUERY = "current_query"
    private const val DEFAULT_QUERY = ""
  }
}