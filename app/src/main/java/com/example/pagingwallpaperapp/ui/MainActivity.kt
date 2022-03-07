package com.example.pagingwallpaperapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pagingwallpaperapp.R
import com.example.pagingwallpaperapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
    navController = navHostFragment.navController

    val appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController,appBarConfiguration)
  }

  override fun navigateUpTo(upIntent: Intent?): Boolean {
    return navController.navigateUp()|| super.navigateUpTo(upIntent)
  }
}