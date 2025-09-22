package com.example.letmecook.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class HomeFragment extends Fragment {

    private TextInputEditText editTextIngredient;
    private ImageButton buttonAddIngredient;
    private ChipGroup chipGroupIngredients;
    private Button buttonSearch;
    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        editTextIngredient = view.findViewById(R.id.editTextIngredient);
        buttonAddIngredient = view.findViewById(R.id.buttonAddIngredient);
        chipGroupIngredients = view.findViewById(R.id.chipGroupIngredients);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        recyclerViewRecipes = view.findViewById(R.id.recyclerViewRecipes);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();
        setupTagInput();

        buttonSearch.setOnClickListener(v -> {
            String ingredients = getIngredientsFromChips();
            if (!ingredients.isEmpty()) {
                searchForRecipes(ingredients);
            } else {
                Toast.makeText(requireContext(), "Please add some ingredients first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTagInput() {
        buttonAddIngredient.setOnClickListener(v -> {
            String ingredientText = editTextIngredient.getText().toString().trim();
            if (!ingredientText.isEmpty()) {
                addChipToGroup(ingredientText);
                editTextIngredient.setText("");
            }
        });
    }

    private void addChipToGroup(String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setClickable(true);
        chip.setCheckable(false);
        chip.setOnCloseIconClickListener(chipGroupIngredients::removeView);
        chipGroupIngredients.addView(chip);
    }

    private String getIngredientsFromChips() {
        List<String> ingredientsList = new ArrayList<>();
        for (int i = 0; i < chipGroupIngredients.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupIngredients.getChildAt(i);
            ingredientsList.add(chip.getText().toString());
        }
        return TextUtils.join(",", ingredientsList);
    }

    private void setupRecyclerView() {
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeAdapter = new RecipeAdapter(requireContext(), new ArrayList<>());
        recyclerViewRecipes.setAdapter(recipeAdapter);
    }

    private void searchForRecipes(String ingredients) {
        progressBar.setVisibility(View.VISIBLE);
        SpoonacularApi apiService = ApiClient.getClient().create(SpoonacularApi.class);
        Call<List<RecipeByIngredientResponse>> call = apiService.searchRecipesByIngredients(
                BuildConfig.SPOONACULAR_API_KEY, ingredients, 20);

        call.enqueue(new Callback<List<RecipeByIngredientResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecipeByIngredientResponse>> call, @NonNull Response<List<RecipeByIngredientResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    recipeAdapter.updateRecipes(response.body());
                } else {
                    Toast.makeText(requireContext(), "No recipes found. Try different ingredients.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecipeByIngredientResponse>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}