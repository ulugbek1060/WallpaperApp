package com.example.pagingwallpaperapp.ui.search

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.pagingwallpaperapp.data.querydata.QueryData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
  private val repository: SearchRepository,
  state: SavedStateHandle
) : ViewModel() {

  private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

  val getQueryList = repository.queryDao.getAllList().asLiveData()

  fun insertQuery(query: QueryData) = viewModelScope.launch {
    repository.queryDao.insert(query)
  }

  fun clearQueryList() = viewModelScope.launch {
    repository.queryDao.delete()
  }

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