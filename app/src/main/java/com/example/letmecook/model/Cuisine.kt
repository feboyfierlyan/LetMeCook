package com.example.letmecook.model

import androidx.annotation.DrawableRes

data class Cuisine(
    val name: String,
    @DrawableRes val imageResId: Int
)