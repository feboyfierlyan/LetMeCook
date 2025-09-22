package com.example.letmecook.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.BuildConfig;
import com.example.letmecook.R;
import com.example.letmecook.adapter.RecipeAdapter;
import com.example.letmecook.api.ApiClient;
import com.example.letmecook.api.SpoonacularApi;
import com.example.letmecook.model.RecipeByIngredientResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText editTextIngredient;
    private ImageButton buttonAddIngredient;
    private ChipGroup chipGroupIngredients;
    private Button buttonSearch;
    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        editTextIngredient = findViewById(R.id.editTextIngredient);
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        chipGroupIngredients = findViewById(R.id.chipGroupIngredients);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        progressBar = findViewById(R.id.progressBar);

        setupRecyclerView();
        setupTagInput();

        buttonSearch.setOnClickListener(v -> {
            // Build the ingredient string from the chips
            String ingredients = getIngredientsFromChips();
            if (!ingredients.isEmpty()) {
                searchForRecipes(ingredients);
            } else {
                Toast.makeText(MainActivity.this, "Please add some ingredients first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up the logic for adding ingredients as tags (chips).
     */
    private void setupTagInput() {
        buttonAddIngredient.setOnClickListener(v -> {
            String ingredientText = editTextIngredient.getText().toString().trim();
            if (!ingredientText.isEmpty()) {
                addChipToGroup(ingredientText);
                editTextIngredient.setText(""); // Clear the input field
            }
        });
    }

    /**
     * Creates and adds a new Chip to the ChipGroup.
     * @param text The ingredient name for the chip.
     */
    private void addChipToGroup(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setClickable(true);
        chip.setCheckable(false);

        // Set a listener to remove the chip when the close icon is clicked
        chip.setOnCloseIconClickListener(view -> {
            chipGroupIngredients.removeView(view);
        });

        chipGroupIngredients.addView(chip);
    }

    /**
     * Iterates through the ChipGroup and builds a comma-separated string of ingredients.
     * @return A string like "chicken,rice,tomato".
     */
    private String getIngredientsFromChips() {
        List<String> ingredientsList = new ArrayList<>();
        for (int i = 0; i < chipGroupIngredients.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupIngredients.getChildAt(i);
            ingredientsList.add(chip.getText().toString());
        }
        return TextUtils.join(",", ingredientsList);
    }

    private void setupRecyclerView() {
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(this, new ArrayList<>());
        recyclerViewRecipes.setAdapter(recipeAdapter);
    }

    private void searchForRecipes(String ingredients) {
        progressBar.setVisibility(View.VISIBLE);
        SpoonacularApi apiService = ApiClient.getClient().create(SpoonacularApi.class);
        Call<List<RecipeByIngredientResponse>> call = apiService.searchRecipesByIngredients(
                BuildConfig.SPOONACULAR_API_KEY, ingredients, 20);

        call.enqueue(new Callback<List<RecipeByIngredientResponse>>() {
            @Override
            public void onResponse(Call<List<RecipeByIngredientResponse>> call, Response<List<RecipeByIngredientResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    recipeAdapter.updateRecipes(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "No recipes found. Try different ingredients.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecipeByIngredientResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}