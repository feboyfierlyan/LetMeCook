package com.example.letmecook.model

import com.google.gson.annotations.SerializedName

// SATU-SATUNYA MODEL UNTUK RESEP
data class Recipe(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("readyInMinutes") val readyInMinutes: Int = 0,
    @SerializedName("aggregateLikes") val aggregateLikes: Int = 0,
    @SerializedName("spoonacularScore") val spoonacularScore: Double = 0.0
)