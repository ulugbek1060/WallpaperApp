package com.example.pagingwallpaperapp.ui.newphotos

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pagingwallpaperapp.R
import com.example.pagingwallpaperapp.data.adapters.DataPagingAdapter
import com.example.pagingwallpaperapp.data.adapters.PhotoLoadStateAdapter
import com.example.pagingwallpaperapp.data.UnsplashPhoto
import com.example.pagingwallpaperapp.databinding.FragmentNewPhotosBinding
import com.example.pagingwallpaperapp.util.RenderScriptProvider
import com.example.pagingwallpaperapp.util.SortOrder
import com.example.pagingwallpaperapp.util.getSortType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewPhotoFragment : Fragment(R.layout.fragment_new_photos),
  DataPagingAdapter.OnItemClickListener {

  private val binding by viewBinding(FragmentNewPhotosBinding::bind)
  private val viewModel: NewPhotoViewModel by viewModels()
  private val renderScriptProvider: RenderScriptProvider by lazy {
    RenderScriptProvider(
      requireContext()
    )
  }
  private val dataAdapter: DataPagingAdapter by lazy {
    DataPagingAdapter(
      this,
      renderScriptProvider
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      recyclerView.apply {
        adapter = dataAdapter.withLoadStateHeaderAndFooter(
          header = PhotoLoadStateAdapter { dataAdapter.retry() },
          footer = PhotoLoadStateAdapter { dataAdapter.retry() }
        )
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
      }

      buttonRetry.setOnClickListener {
        dataAdapter.retry()
      }

      viewModel.photos.observe(viewLifecycleOwner) {
        dataAdapter.submitData(viewLifecycleOwner.lifecycle, it)
      }
    }

    dataAdapter.addLoadStateListener { loadState ->
      binding.apply {
        progressBar.isVisible = loadState.source.refresh is LoadState.Loading
        recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
        textViewError.isVisible = loadState.source.refresh is LoadState.Error
        buttonRetry.isVisible = loadState.source.refresh is LoadState.Error

        if (loadState.source.refresh is LoadState.NotLoading &&
          loadState.append.endOfPaginationReached &&
          dataAdapter.itemCount > 1
        ) {
          recyclerView.isVisible = false
          textViewEmpty.isVisible = true
        } else {
          textViewEmpty.isVisible = false
        }
      }
    }

    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_new_photo, menu)

    viewLifecycleOwner.lifecycleScope.launch {
      when (val orderFromPreferences = viewModel.getOrderFromPreferences()) {
        SortOrder.POPULAR -> {
          menu.findItem(R.id.action_sort_popular).isChecked =
            getSortType(orderFromPreferences).second
        }
        SortOrder.LATEST -> {
          menu.findItem(R.id.action_sort_latest).isChecked =
            getSortType(orderFromPreferences).second
        }
        SortOrder.OLDEST -> {
          menu.findItem(R.id.action_sort_oldest).isChecked =
            getSortType(orderFromPreferences).second
        }
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_to_search -> {
        findNavController().navigate(R.id.searchFragment)
        true
      }
      R.id.action_sort_latest -> {
        changesOnType(SortOrder.LATEST)
        item.isChecked = !item.isChecked
        true
      }
      R.id.action_sort_oldest -> {
        changesOnType(SortOrder.OLDEST)
        item.isChecked = !item.isChecked
        true
      }
      R.id.action_sort_popular -> {
        changesOnType(SortOrder.POPULAR)
        item.isChecked = !item.isChecked
        true
      }
      else -> {
        super.onOptionsItemSelected(item)
      }
    }
  }

  private fun changesOnType(sortOrder: SortOrder) {
    viewModel.updateSortOrder(sortOrder)
    dataAdapter.refresh()
    binding.recyclerView.scrollToPosition(0)
  }

  override fun onItemClick(unsplashPhoto: UnsplashPhoto) {
    val action = NewPhotoFragmentDirections.actionNewPhotoFragmentToDetailFragment(unsplashPhoto)
    findNavController().navigate(action)
  }
}