package com.example.letmecook.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.letmecook.R
import com.example.letmecook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), HomeFragment.OnSearchBarClickedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Hubungkan BottomNavigationView dengan NavController.
        // Ini akan menangani perpindahan fragment secara otomatis.
        binding.bottomNavigation.setupWithNavController(navController)

        // LOGIKA BARU UNTUK INDIKATOR GARIS
        // Dijalankan setelah layout selesai diukur untuk mendapatkan lebar yang benar.
        binding.bottomNavigation.post {
            binding.bottomNavActiveLineIndicator.visibility = View.VISIBLE
            updateLineIndicatorPosition(binding.bottomNavigation.selectedItemId, animate = false)
        }

        // Tambahkan listener untuk mendeteksi perpindahan halaman (fragment).
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Animasikan garis ke posisi item menu yang sesuai dengan halaman baru.
            updateLineIndicatorPosition(destination.id, animate = true)
        }
    }

    /**
     * Menghitung dan menganimasikan posisi serta lebar dari garis indikator.
     */
    private fun updateLineIndicatorPosition(selectedItemId: Int, animate: Boolean) {
        val menu = binding.bottomNavigation.menu
        val menuSize = menu.size()
        if (menuSize == 0) return

        // Hitung lebar setiap item menu
        val itemWidth = binding.bottomNavigation.width / menuSize

        // Temukan indeks item menu yang dipilih
        var selectedIndex = -1
        for (i in 0 until menuSize) {
            if (menu.getItem(i).itemId == selectedItemId) {
                selectedIndex = i
                break
            }
        }
        if (selectedIndex == -1) return

        // Hitung posisi X baru untuk garis
        val targetX = (selectedIndex * itemWidth).toFloat()

        // Sesuaikan lebar garis agar sama dengan lebar item
        val layoutParams = binding.bottomNavActiveLineIndicator.layoutParams
        layoutParams.width = itemWidth
        binding.bottomNavActiveLineIndicator.layoutParams = layoutParams

        if (animate) {
            // Animasikan perpindahan garis secara horizontal
            ObjectAnimator.ofFloat(binding.bottomNavActiveLineIndicator, "translationX", targetX).apply {
                duration = 200 // Durasi animasi dalam milidetik
                start()
            }
        } else {
            // Pindahkan garis langsung ke posisi tanpa animasi (untuk pemuatan awal)
            binding.bottomNavActiveLineIndicator.translationX = targetX
        }
    }

    /**
     * Fungsi dari interface yang dipanggil oleh HomeFragment.
     */
    override fun onSearchBarClicked() {
        // Mengganti item terpilih di BottomNavigationView akan otomatis memicu
        // NavController dan addOnDestinationChangedListener.
        binding.bottomNavigation.selectedItemId = R.id.navigation_search
    }
}