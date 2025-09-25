package com.example.letmecook.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class WishlistFragment extends Fragment {

    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter wishlistAdapter;
    private TextView textViewEmptyWishlist;
    private RecipeDao recipeDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewWishlist = view.findViewById(R.id.recyclerViewWishlist);
        textViewEmptyWishlist = view.findViewById(R.id.textViewEmptyWishlist);
        recipeDao = AppDatabase.getDatabase(requireContext()).recipeDao();

        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWishlist(); // Muat ulang data setiap kali fragment ini ditampilkan
    }

    private void setupRecyclerView() {
        recyclerViewWishlist.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                    wishlistAdapter = new WishlistAdapter(requireContext(), wishlist, recipeDao);
                    recyclerViewWishlist.setAdapter(wishlistAdapter);
                }
            });
        });
    }
}