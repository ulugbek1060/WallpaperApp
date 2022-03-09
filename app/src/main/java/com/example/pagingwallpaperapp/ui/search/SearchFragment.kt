package com.example.pagingwallpaperapp.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pagingwallpaperapp.R
import com.example.pagingwallpaperapp.data.UnsplashPhoto
import com.example.pagingwallpaperapp.data.adapters.DataPagingAdapter
import com.example.pagingwallpaperapp.data.adapters.PhotoLoadStateAdapter
import com.example.pagingwallpaperapp.data.adapters.QueryCacheAdapter
import com.example.pagingwallpaperapp.data.querydata.QueryData
import com.example.pagingwallpaperapp.databinding.FragmentSearchBinding
import com.example.pagingwallpaperapp.util.RenderScriptProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), DataPagingAdapter.OnItemClickListener,
  QueryCacheAdapter.OnItemClickQuery {

  private val binding by viewBinding(FragmentSearchBinding::bind)
  private val viewModel: SearchViewModel by viewModels()
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
  private val queryAdapter: QueryCacheAdapter by lazy {
    QueryCacheAdapter(this)
  }

  private lateinit var searchView: SearchView

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.apply {
      recyclerViewQuery.apply {
        adapter = queryAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        setHasFixedSize(true)
        itemAnimator = null
      }

      recyclerView.apply {
        adapter = dataAdapter.withLoadStateHeaderAndFooter(
          header = PhotoLoadStateAdapter { dataAdapter.retry() },
          footer = PhotoLoadStateAdapter { dataAdapter.retry() }
        )
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
        itemAnimator = null
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

        textViewClear.setOnClickListener {
          requestFocusToSearchView("")
          viewModel.clearQueryList()
        }

        viewModel.getQueryList.observe(viewLifecycleOwner) {
          layoutContainer.isVisible = it.isNotEmpty()
          queryAdapter.submitList(it)
        }
      }
    }



    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_search, menu)

    val itemSearch = menu.findItem(R.id.action_search)
    searchView = itemSearch.actionView as SearchView
    searchView.setOnQueryTextListener(searchListener)

  }

  private val searchListener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
      if (query != null) {
        binding.recyclerView.scrollToPosition(0)
        viewModel.searchPhoto(query)
        searchView.clearFocus()
        viewModel.insertQuery(QueryData(query))
      }
      return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
      return true
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return if (item.itemId == android.R.id.home) {
      findNavController().popBackStack()
      true
    } else {
      super.onOptionsItemSelected(item)
    }
  }

  override fun onItemClick(unsplashPhoto: UnsplashPhoto) {
    val action = SearchFragmentDirections.actionGalleryFragmentToDetailFragment(unsplashPhoto)
    findNavController().navigate(action)
  }

  override fun onItemClick(query: QueryData) {
    requestFocusToSearchView(query.text)
  }

  private fun requestFocusToSearchView(text: String) {
    searchView.requestFocus()
    searchView.setQuery(text, true)
  }
}