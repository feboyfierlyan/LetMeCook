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
import com.example.letmecook.model.RecipeByIngredientResponse;
import com.example.letmecook.ui.RecipeDetailActivity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<RecipeByIngredientResponse> recipeList;
    private Context context;
    private RecipeDao recipeDao;

    public RecipeAdapter(Context context, List<RecipeByIngredientResponse> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        // Initialize DAO
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
        RecipeByIngredientResponse recipe = recipeList.get(position);
        holder.textViewTitle.setText(recipe.getTitle());

        Glide.with(context)
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageViewRecipe);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            context.startActivity(intent);
        });

        // Wishlist logic
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Check if recipe is in wishlist and set the icon state
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

        holder.buttonFavorite.setOnClickListener(v -> {
            executor.execute(() -> {
                if ("unfavorited".equals(holder.buttonFavorite.getTag())) {
                    // Add to wishlist
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
                    // Remove from wishlist
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

    public void updateRecipes(List<RecipeByIngredientResponse> newRecipes) {
        this.recipeList.clear();
        this.recipeList.addAll(newRecipes);
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRecipe;
        TextView textViewTitle;
        ImageButton buttonFavorite;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            textViewTitle = itemView.findViewById(R.id.textViewRecipeTitle);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }
    }
}