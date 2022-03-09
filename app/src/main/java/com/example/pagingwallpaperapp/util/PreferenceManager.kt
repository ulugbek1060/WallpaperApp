package com.example.pagingwallpaperapp.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.pagingwallpaperapp.api.UnsplashApi.Companion.LATEST
import com.example.pagingwallpaperapp.api.UnsplashApi.Companion.OLDEST
import com.example.pagingwallpaperapp.api.UnsplashApi.Companion.POPULAR
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val DATA_STORE_NAME = "settings"

enum class SortOrder { LATEST, OLDEST, POPULAR }

@Singleton
class PreferenceManager @Inject constructor(
  @ApplicationContext context: Context
) {
  private val dataStore = context.createDataStore(DATA_STORE_NAME)

  val preferenceFlow = dataStore.data
    .catch { exception ->
      if (exception is IOException) {
        emit(emptyPreferences())
      } else {
        throw exception
      }
    }
    .map { preferences ->
      SortOrder.valueOf(
        preferences[PreferencesKey.SORT_ORDER_KEY] ?: SortOrder.POPULAR.name
      )
    }

  suspend fun updateSortOrder(sortOrder: SortOrder) {
    dataStore.edit {
      it[PreferencesKey.SORT_ORDER_KEY] = sortOrder.name
    }
  }

  private object PreferencesKey {
    val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
  }
}

fun getSortType(sortOrder: SortOrder) = when (sortOrder) {
  SortOrder.POPULAR -> Pair(POPULAR, true)
  SortOrder.LATEST -> Pair(LATEST, true)
  SortOrder.OLDEST -> Pair(OLDEST, true)
}
