package com.example.pagingwallpaperapp.data

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class UnsplashPhoto(
  val id: String,
  var description: String,
  val urls: UnsplashPhotoUrls,
  val user: UnsplashUser
) : Parcelable {

  @Parcelize
  data class UnsplashPhotoUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val thumb: String
  ) : Parcelable

  @Parcelize
  data class UnsplashUser(
    val name: String,
    val username: String
  ) : Parcelable {
    val attributes get() = "https://unsplash.com/$username?utm_source=ImageSearchApp&utm_medium=referral"
  }
}
