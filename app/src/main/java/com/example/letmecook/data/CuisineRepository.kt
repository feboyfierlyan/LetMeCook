package com.example.letmecook.data

import com.example.letmecook.R
import com.example.letmecook.model.Cuisine

// object membuat ini menjadi Singleton, artinya hanya ada satu instance dari kelas ini
object CuisineRepository {

    fun getAllCuisines(): List<Cuisine> {
        return listOf(
            Cuisine("Italian", R.drawable.image_italian),
            Cuisine("Chinese", R.drawable.image_chinese),
            Cuisine("Mexican", R.drawable.image_mexican),
            Cuisine("Indian", R.drawable.image_indian),
            Cuisine("Japanese", R.drawable.image_japanese),
            Cuisine("French", R.drawable.image_french),
            Cuisine("Spanish", R.drawable.image_spanish),
            Cuisine("American", R.drawable.image_american),
            Cuisine("Thai", R.drawable.image_thai),
            Cuisine("Vietnamese", R.drawable.image_vietnamese),
            Cuisine("Korean", R.drawable.image_korean),
            Cuisine("German", R.drawable.image_german)
            // Tambahkan masakan lain & ganti placeholder dengan gambar Anda
        )
    }
}