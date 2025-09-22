package com.example.letmecook.api;

import com.example.letmecook.model.ComplexSearchResponse;
import com.example.letmecook.model.RecipeByIngredientResponse;
import com.example.letmecook.model.RecipeDetailsResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {

    @GET("recipes/findByIngredients")
    Call<List<RecipeByIngredientResponse>> searchRecipesByIngredients(
            @Query("apiKey") String apiKey,
            @Query("ingredients") String ingredients,
            @Query("number") int number
    );

    @GET("recipes/{id}/information")
    Call<RecipeDetailsResponse> getRecipeDetails(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );
    @GET("recipes/complexSearch")

    Call<ComplexSearchResponse> searchRecipesByCuisine(
            @Query("apiKey") String apiKey,
            @Query("cuisine") String cuisine,
            @Query("number") int number
    );
}