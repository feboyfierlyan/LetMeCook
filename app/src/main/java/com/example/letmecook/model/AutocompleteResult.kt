package com.example.letmecook.model

import com.google.gson.annotations.SerializedName

data class AutocompleteResult(
    @SerializedName("name") val name: String
)