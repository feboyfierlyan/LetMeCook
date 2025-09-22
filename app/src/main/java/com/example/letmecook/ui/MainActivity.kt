package com.example.letmecook.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.letmecook.R
import com.example.letmecook.databinding.ActivityMainBinding

// Implementasikan interface dari HomeFragment
class MainActivity : AppCompatActivity(), HomeFragment.OnSearchBarClickedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

    // Override fungsi dari interface
    override fun onSearchBarClicked() {
        // Aksi yang dijalankan saat search bar diklik
        binding.bottomNavigation.selectedItemId = R.id.navigation_search
    }
}