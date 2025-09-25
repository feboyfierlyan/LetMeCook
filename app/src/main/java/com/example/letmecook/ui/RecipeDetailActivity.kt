package com.example.letmecook.ui

import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.letmecook.BuildConfig
import com.example.letmecook.R
import com.example.letmecook.api.ApiClient
import com.example.letmecook.api.SpoonacularApi
import com.example.letmecook.databinding.ActivityRecipeDetailBinding
import com.example.letmecook.model.ExtendedIngredient
import com.example.letmecook.model.RecipeDetailsResponse
import com.example.letmecook.model.Step
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar dengan tombol kembali
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Ambil ID resep dari Intent
        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            fetchRecipeDetails(recipeId)
        } else {
            Toast.makeText(this, "Error: Recipe ID not found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Handle klik tombol kembali di toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun fetchRecipeDetails(recipeId: Int) {
        val apiService = ApiClient.getClient().create(SpoonacularApi::class.java)
        val call = apiService.getRecipeDetails(recipeId, BuildConfig.SPOONACULAR_API_KEY)

        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    displayRecipeDetails(response.body()!!)
                } else {
                    Toast.makeText(this@RecipeDetailActivity, "Failed to load details.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                Toast.makeText(this@RecipeDetailActivity, "Network Error: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayRecipeDetails(recipe: RecipeDetailsResponse) {
        binding.collapsingToolbar.title = recipe.title
        binding.recipeTitle.text = recipe.title
        binding.recipeDescription.text = Html.fromHtml(recipe.summary, Html.FROM_HTML_MODE_COMPACT)

        Glide.with(this)
            .load(recipe.image)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.recipeBannerImage)

        // Format dan tampilkan ingredients
        val ingredientsBuilder = StringBuilder()
        recipe.extendedIngredients?.forEach { ingredient ->
            ingredientsBuilder.append("• ").append(ingredient.original).append("\n")
        }
        binding.recipeIngredients.text = ingredientsBuilder.toString()

        // Anda perlu menambahkan TextView untuk instructions di layout jika ingin menampilkannya
    }
}