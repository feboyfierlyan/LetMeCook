package com.example.letmecook.ui;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.letmecook.BuildConfig;
import com.example.letmecook.R;
import com.example.letmecook.api.ApiClient;
import com.example.letmecook.api.SpoonacularApi;
import com.example.letmecook.model.ExtendedIngredient;
import com.example.letmecook.model.RecipeDetailsResponse;
import com.example.letmecook.model.Step;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {
    private ImageView imageViewDetail;
    private TextView textViewDetailTitle, textViewDetailSummary, textViewDetailIngredients, textViewDetailInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        imageViewDetail = findViewById(R.id.imageViewDetail);
        textViewDetailTitle = findViewById(R.id.textViewDetailTitle);
        textViewDetailSummary = findViewById(R.id.textViewDetailSummary);
        textViewDetailIngredients = findViewById(R.id.textViewDetailIngredients);
        textViewDetailInstructions = findViewById(R.id.textViewDetailInstructions);

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        if (recipeId != -1) {
            fetchRecipeDetails(recipeId);
        } else {
            Toast.makeText(this, "Error: Recipe ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchRecipeDetails(int recipeId) {
        SpoonacularApi apiService = ApiClient.getClient().create(SpoonacularApi.class);
        Call<RecipeDetailsResponse> call = apiService.getRecipeDetails(recipeId, BuildConfig.SPOONACULAR_API_KEY);

        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayRecipeDetails(response.body());
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "Failed to load details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecipeDetails(RecipeDetailsResponse recipe) {
        textViewDetailTitle.setText(recipe.getTitle());
        textViewDetailSummary.setText(Html.fromHtml(recipe.getSummary(), Html.FROM_HTML_MODE_COMPACT));

        Glide.with(this)
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(imageViewDetail);

        // Format and display ingredients
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (ExtendedIngredient ingredient : recipe.getExtendedIngredients()) {
            ingredientsBuilder.append("• ").append(ingredient.getOriginal()).append("\n");
        }
        textViewDetailIngredients.setText(ingredientsBuilder.toString());

        // Format and display instructions
        StringBuilder instructionsBuilder = new StringBuilder();
        if (recipe.getAnalyzedInstructions() != null && !recipe.getAnalyzedInstructions().isEmpty()) {
            for (Step step : recipe.getAnalyzedInstructions().get(0).getSteps()) {
                instructionsBuilder.append(step.getNumber()).append(". ").append(step.getStep()).append("\n\n");
            }
        } else {
            instructionsBuilder.append("No instructions available.");
        }
        textViewDetailInstructions.setText(instructionsBuilder.toString());
    }
}