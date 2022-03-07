package com.example.pagingwallpaperapp.api

import com.example.pagingwallpaperapp.BuildConfig
import com.example.pagingwallpaperapp.data.UnsplashPhoto
import com.example.pagingwallpaperapp.data.UnsplashResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface UnsplashApi {

  companion object {
    const val BASE_URL = "https://api.unsplash.com/"
    const val CLIENT_ID = BuildConfig.UNSPLASH_ACCES_KEY

    const val LATEST = "latest"
    const val OLDEST = "oldest"
    const val POPULAR = "popular"
  }

  @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
  @GET("search/photos")
  suspend fun searchPhotos(
    @Query("query") query: String,
    @Query("page") page: Int,
    @Query("per_page") perPage: Int
  ): Response<UnsplashResponse>

  @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
  @GET("photos")
  suspend fun newPhotosList(
    @Query("page") page: Int,
    @Query("per_page") perPage: Int,
    @Query("order_by") orderBy: String = POPULAR
  ): Response<List<UnsplashPhoto>>

}