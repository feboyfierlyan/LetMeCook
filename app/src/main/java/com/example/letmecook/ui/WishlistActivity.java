package com.example.letmecook.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.R;
import com.example.letmecook.adapter.WishlistAdapter;
import com.example.letmecook.database.AppDatabase;
import com.example.letmecook.database.RecipeDao;
import com.example.letmecook.database.WishlistRecipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter wishlistAdapter;
    private TextView textViewEmptyWishlist;
    private RecipeDao recipeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerViewWishlist = findViewById(R.id.recyclerViewWishlist);
        textViewEmptyWishlist = findViewById(R.id.textViewEmptyWishlist);
        recipeDao = AppDatabase.getDatabase(this).recipeDao();

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishlist();
    }

    private void setupRecyclerView() {
        recyclerViewWishlist.setLayoutManager(new LinearLayoutManager(this));
        // Adapter akan di-set setelah data dimuat
    }

    private void loadWishlist() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<WishlistRecipe> wishlist = recipeDao.getAllWishlistRecipes();
            handler.post(() -> {
                if (wishlist == null || wishlist.isEmpty()) {
                    textViewEmptyWishlist.setVisibility(View.VISIBLE);
                    recyclerViewWishlist.setVisibility(View.GONE);
                } else {
                    textViewEmptyWishlist.setVisibility(View.GONE);
                    recyclerViewWishlist.setVisibility(View.VISIBLE);
                    wishlistAdapter = new WishlistAdapter(this, wishlist, recipeDao);
                    recyclerViewWishlist.setAdapter(wishlistAdapter);
                }
            });
        });
    }
}