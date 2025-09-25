package com.example.letmecook.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.R;
import com.example.letmecook.database.AppDatabase;
import com.example.letmecook.database.RecipeDao;
import com.example.letmecook.database.WishlistRecipe;
import com.example.letmecook.model.Recipe;
import com.example.letmecook.ui.RecipeDetailActivity;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private Context context;
    private RecipeDao recipeDao; // DAO untuk interaksi database

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        // Inisialisasi DAO dari database
        this.recipeDao = AppDatabase.getDatabase(context).recipeDao();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.textViewTitle.setText(recipe.getTitle());

        // Menampilkan detail waktu dan rating
        holder.textViewCookingTime.setText(recipe.getReadyInMinutes() + " min");
        double ratingValue = recipe.getSpoonacularScore() / 20.0;
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedRating = df.format(ratingValue);
        String likes = recipe.getAggregateLikes() > 1000
                ? (recipe.getAggregateLikes() / 1000) + "rb+ rating"
                : recipe.getAggregateLikes() + " rating";
        holder.textViewRating.setText(formattedRating + " • " + likes);

        Glide.with(context)
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageViewRecipe);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            context.startActivity(intent);
        });

        // --- LOGIKA BARU UNTUK TOMBOL FAVORIT ---
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Cek status favorit saat kartu ditampilkan
        executor.execute(() -> {
            WishlistRecipe existingRecipe = recipeDao.getWishlistRecipeById(recipe.getId());
            handler.post(() -> {
                if (existingRecipe != null) {
                    holder.buttonFavorite.setImageResource(R.drawable.ic_favorite_filled);
                    holder.buttonFavorite.setTag("favorited");
                } else {
                    holder.buttonFavorite.setImageResource(R.drawable.ic_favorite_border);
                    holder.buttonFavorite.setTag("unfavorited");
                }
            });
        });

        // Tambahkan listener untuk klik tombol favorit
        holder.buttonFavorite.setOnClickListener(v -> {
            executor.execute(() -> {
                if ("unfavorited".equals(holder.buttonFavorite.getTag())) {
                    // Tambahkan ke wishlist
                    WishlistRecipe newWish = new WishlistRecipe();
                    newWish.setRecipeId(recipe.getId());
                    newWish.setTitle(recipe.getTitle());
                    newWish.setImageUrl(recipe.getImage());
                    recipeDao.insert(newWish);

                    handler.post(() -> {
                        holder.buttonFavorite.setImageResource(R.drawable.ic_favorite_filled);
                        holder.buttonFavorite.setTag("favorited");
                        Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Hapus dari wishlist
                    WishlistRecipe toDelete = recipeDao.getWishlistRecipeById(recipe.getId());
                    if (toDelete != null) {
                        recipeDao.delete(toDelete);
                    }
                    handler.post(() -> {
                        holder.buttonFavorite.setImageResource(R.drawable.ic_favorite_border);
                        holder.buttonFavorite.setTag("unfavorited");
                        Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        if (newRecipes != null) {
            this.recipeList.clear();
            this.recipeList.addAll(newRecipes);
            notifyDataSetChanged();
        }
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRecipe;
        TextView textViewTitle, textViewCookingTime, textViewRating;
        ImageButton buttonFavorite;
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            textViewTitle = itemView.findViewById(R.id.textViewRecipeTitle);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
            textViewCookingTime = itemView.findViewById(R.id.text_view_cooking_time);
            textViewRating = itemView.findViewById(R.id.text_view_rating);
        }
    }
}