package com.example.pagingwallpaperapp.ui.newphotos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagingwallpaperapp.api.UnsplashApi.Companion.POPULAR
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewPhotoViewModel @Inject constructor(
  private val repository: NewPhotoRepository,
  private val state: SavedStateHandle
) : ViewModel() {

  private val mutableList = state.getLiveData(CURRENT_STATE_KEY, TEMPORARY_STATE)


  val photos = mutableList.switchMap {
    repository.getListOfPhoto(it).cachedIn(viewModelScope)
  }

  fun changesOrderBy(order: String) {
    mutableList.value = order
  }

  companion object {
    const val CURRENT_STATE_KEY = "current_tate"
    const val TEMPORARY_STATE = POPULAR
  }

  sealed class TypeChangesEvent {

  }
}