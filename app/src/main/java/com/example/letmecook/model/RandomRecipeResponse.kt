package com.example.letmecook.model

import com.google.gson.annotations.SerializedName

// Model data untuk menampung response dari endpoint resep acak
data class RandomRecipeResponse(
    @SerializedName("recipes") val recipes: List<Recipe>
)