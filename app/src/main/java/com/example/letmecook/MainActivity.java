package com.example.letmecook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.adapters.MealAdapter;
import com.example.letmecook.api.ApiClient;
import com.example.letmecook.api.ApiService;
import com.example.letmecook.models.MealResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etIngredients;
    private Button btnSearch;
    private RecyclerView rvRecipes;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIngredients = findViewById(R.id.etIngredients);
        btnSearch = findViewById(R.id.btnSearch);
        rvRecipes = findViewById(R.id.rvRecipes);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        mealAdapter = new MealAdapter(this, null);
        rvRecipes.setAdapter(mealAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ingredients = etIngredients.getText().toString().trim();
                if (ingredients.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Masukkan bahan dulu!", Toast.LENGTH_SHORT).show();
                } else {
                    searchMeals(ingredients);
                }
            }
        });
    }

    private void searchMeals(String ingredients) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MealResponse> call = apiService.getMealsByIngredients(ingredients);

        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter.setMeals(response.body().getMeals());
                } else {
                    Toast.makeText(MainActivity.this, "Resep tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
