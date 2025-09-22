package com.example.letmecook.model

import com.google.gson.annotations.SerializedName

// Model data utama untuk response dari endpoint complexSearch
data class ComplexSearchResponse(
    @SerializedName("results") val results: List<RecipeResult>
)

// Model data untuk setiap item resep di dalam 'results'
data class RecipeResult(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String
)