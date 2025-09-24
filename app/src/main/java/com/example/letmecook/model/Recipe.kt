package com.example.letmecook.model

import com.google.gson.annotations.SerializedName

// Model data untuk satu resep
data class Recipe(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String
)