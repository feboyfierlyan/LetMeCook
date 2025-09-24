package com.example.letmecook.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letmecook.BuildConfig
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.model.RandomRecipeResponse
import com.example.letmecook.model.Recipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _recommendedRecipes = MutableLiveData<List<Recipe>>()
    val recommendedRecipes: LiveData<List<Recipe>> = _recommendedRecipes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchRecommendedRecipesIfNeeded() {
        if (_recommendedRecipes.value.isNullOrEmpty()) {
            fetchFromApi()
        }
    }

    private fun fetchFromApi() {
        _isLoading.value = true
        val apiService = ApiClient.getClient().create(SpoonacularApi::class.java)
        val call = apiService.getRandomRecipes(BuildConfig.SPOONACULAR_API_KEY, 10)

        call.enqueue(object : Callback<RandomRecipeResponse> {
            override fun onResponse(
                call: Call<RandomRecipeResponse>,
                response: Response<RandomRecipeResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _recommendedRecipes.value = response.body()!!.recipes
                } else {
                    _error.value = "Failed to fetch recommended recipes."
                }
            }

            override fun onFailure(call: Call<RandomRecipeResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Network Error: ${t.message}"
            }
        })
    }
}