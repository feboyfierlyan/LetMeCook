package com.example.letmecook.model

import com.google.gson.annotations.SerializedName

// Model untuk response dari endpoint complexSearch
data class ComplexSearchResponse(
    @SerializedName("results") val results: List<Recipe>
)

// Model untuk response dari endpoint resep acak
data class RandomRecipeResponse(
    @SerializedName("recipes") val recipes: List<Recipe>
)