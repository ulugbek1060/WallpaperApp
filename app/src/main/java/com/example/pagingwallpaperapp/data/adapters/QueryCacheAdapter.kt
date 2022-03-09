package com.example.pagingwallpaperapp.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingwallpaperapp.data.querydata.QueryData
import com.example.pagingwallpaperapp.databinding.ItemQueryBinding


class QueryCacheAdapter(private val listener: OnItemClickQuery) :
  ListAdapter<QueryData, QueryCacheAdapter.Vh>(DiffUtilQuery()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    Vh(ItemQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: Vh, position: Int) {
    val item = getItem(position)
    if (item != null) {
      holder.bind(item)
    }
  }

  inner class Vh(private val binding: ItemQueryBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
      binding.root.setOnClickListener {
        val item = getItem(absoluteAdapterPosition)
        if (item != null) {
          listener.onItemClick(item)
        }
      }
    }

    fun bind(query: QueryData) {
      binding.textViewQuery.text = query.text
    }
  }

  class DiffUtilQuery : DiffUtil.ItemCallback<QueryData>() {
    override fun areItemsTheSame(oldItem: QueryData, newItem: QueryData): Boolean {
      return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: QueryData, newItem: QueryData): Boolean {
      return oldItem == newItem
    }

  }

  interface OnItemClickQuery {
    fun onItemClick(query: QueryData)
  }
}