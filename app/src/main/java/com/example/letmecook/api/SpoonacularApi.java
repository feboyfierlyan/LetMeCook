package com.example.letmecook.api;

import com.example.letmecook.model.AutocompleteResult;
import com.example.letmecook.model.ComplexSearchResponse;
import com.example.letmecook.model.RandomRecipeResponse;
import com.example.letmecook.model.RecipeDetailsResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {

    @GET("recipes/complexSearch")
    Call<ComplexSearchResponse> searchRecipesComplex(
            @Query("apiKey") String apiKey,
            @Query("includeIngredients") String ingredients,
            @Query("addRecipeInformation") boolean addInfo,
            @Query("number") int number
    );

    // PASTIKAN FUNGSI INI MEMILIKI 4 PARAMETER
    @GET("recipes/complexSearch")
    Call<ComplexSearchResponse> searchRecipesByCuisine(
            @Query("apiKey") String apiKey,
            @Query("cuisine") String cuisine,
            @Query("addRecipeInformation") boolean addInfo, // Parameter ini yang hilang
            @Query("number") int number
    );

    @GET("recipes/random")
    Call<RandomRecipeResponse> getRandomRecipes(
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    @GET("food/ingredients/autocomplete")
    Call<List<AutocompleteResult>> autocompleteIngredients(
            @Query("apiKey") String apiKey,
            @Query("query") String query,
            @Query("number") int number
    );

    @GET("recipes/{id}/information")
    Call<RecipeDetailsResponse> getRecipeDetails(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );
}