package com.example.letmecook.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText editTextIngredients;
    private Button buttonSearch;
    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIngredients = findViewById(R.id.editTextIngredients);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        progressBar = findViewById(R.id.progressBar);

        setupRecyclerView();

        buttonSearch.setOnClickListener(v -> {
            String ingredients = editTextIngredients.getText().toString().trim();
            if (!ingredients.isEmpty()) {
                searchForRecipes(ingredients);
            } else {
                Toast.makeText(MainActivity.this, "Please enter some ingredients", Toast.LENGTH_SHORT).show();
            }
        });
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