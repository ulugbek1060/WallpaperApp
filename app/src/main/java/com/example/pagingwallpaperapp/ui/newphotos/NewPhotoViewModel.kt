package com.example.pagingwallpaperapp.ui.newphotos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pagingwallpaperapp.util.SortOrder
import com.example.pagingwallpaperapp.util.getSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPhotoViewModel @Inject constructor(
  private val repository: NewPhotoRepository
) : ViewModel() {

  private val photosFlow = repository.preferences.preferenceFlow
    .flatMapLatest { sortOrder ->
      repository.getListOfPhoto(getSortType(sortOrder).first)
        .asFlow()
        .cachedIn(viewModelScope)
    }

  val photos = photosFlow.asLiveData()

  suspend fun getOrderFromPreferences(): SortOrder {
    return repository.preferences.preferenceFlow.first()
  }

  fun updateSortOrder(sortOrder: SortOrder) = viewModelScope.launch {
    repository.preferences.updateSortOrder(sortOrder)
  }
}