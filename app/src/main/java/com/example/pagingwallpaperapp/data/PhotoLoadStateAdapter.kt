package com.example.pagingwallpaperapp.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingwallpaperapp.databinding.PhotoLoadStateFooterBinding

class PhotoLoadStateAdapter(private val retry: () -> Unit) :
  LoadStateAdapter<PhotoLoadStateAdapter.LoadStateHolder>() {


  override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateHolder =
    LoadStateHolder(
      PhotoLoadStateFooterBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )

  override fun onBindViewHolder(holder: LoadStateHolder, loadState: LoadState) {
    holder.bind(loadState)
  }

  inner class LoadStateHolder(private val binding: PhotoLoadStateFooterBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
      binding.buttonRetry.setOnClickListener {
        retry.invoke()
      }
    }

    fun bind(loadState: LoadState) {
      binding.apply {
        progressBar.isVisible = loadState is LoadState.Loading
        buttonRetry.isVisible = loadState !is LoadState.Loading
        textViewError.isVisible = loadState !is LoadState.Loading
      }
    }
  }
}