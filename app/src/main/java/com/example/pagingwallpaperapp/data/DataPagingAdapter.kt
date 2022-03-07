package com.example.pagingwallpaperapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pagingwallpaperapp.R
import com.example.pagingwallpaperapp.databinding.ItemUnsplashPhotoBinding
import com.example.pagingwallpaperapp.util.RenderScriptBlur
import com.example.pagingwallpaperapp.util.RenderScriptProvider

class DataPagingAdapter(
  private val listener: OnItemClickListener,
  private val renderScriptProvider: RenderScriptProvider
) :
  PagingDataAdapter<UnsplashPhoto, DataPagingAdapter.Vh>(PHOTO_COMPARATOR) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh =
    Vh(ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: Vh, position: Int) {
    val currentItem = getItem(position)
    if (currentItem != null) {
      holder.bind(currentItem)
    }
  }

  inner class Vh(private val binding: ItemUnsplashPhotoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
      binding.root.setOnClickListener {
        val position = absoluteAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
          val item = getItem(position)
          if (item != null) {
            if (item.description == null) {
              item.description = ""
            }
            listener.onItemClick(item)
          }
        }
      }
    }

    fun bind(photo: UnsplashPhoto) {
      binding.apply {
        Glide.with(itemView)
          .load(photo.urls.regular)
          .error(R.drawable.ic_error)
          .into(imageView)
        textViewUsername.text = photo.user.username
        imageView.cornerRadius = 30.toFloat()
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.clipToOutline = true
        cosmonautShadow.blurScript = RenderScriptBlur(renderScriptProvider)

      }
    }
  }

  companion object {
    val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
      override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean {
        return oldItem == newItem
      }
    }
  }

  interface OnItemClickListener {
    fun onItemClick(unsplashPhoto: UnsplashPhoto)
  }
}