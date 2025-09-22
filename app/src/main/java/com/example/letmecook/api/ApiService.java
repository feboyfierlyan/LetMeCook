package com.example.letmecook.api;

import com.example.letmecook.models.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // TheMealDB API untuk filter berdasarkan bahan
    @GET("filter.php")
    Call<MealResponse> getMealsByIngredients(@Query("i") String ingredients);
}
