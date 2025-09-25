package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;

public class ExtendedIngredient {
    @SerializedName("original")
    private String original;

    public String getOriginal() { return original; }
}