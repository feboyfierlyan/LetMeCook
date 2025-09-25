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
import com.example.letmecook.database.RecipeDao;
import com.example.letmecook.database.WishlistRecipe;
import com.example.letmecook.ui.RecipeDetailActivity; // IMPORT INI YANG DIPERLUKAN UNTUK MEMPERBAIKI ERROR
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private Context context;
    private List<WishlistRecipe> wishlist;
    private RecipeDao recipeDao;

    public WishlistAdapter(Context context, List<WishlistRecipe> wishlist, RecipeDao recipeDao) {
        this.context = context;
        this.wishlist = wishlist;
        this.recipeDao = recipeDao;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistRecipe recipe = wishlist.get(position);
        holder.textViewTitle.setText(recipe.getTitle());

        Glide.with(context)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageViewRecipe);

        // Set icon ke status sudah difavoritkan
        holder.buttonFavorite.setImageResource(R.drawable.ic_favorite_filled);

        holder.itemView.setOnClickListener(v -> {
            // Baris ini sekarang akan berfungsi karena sudah di-import
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getRecipeId());
            context.startActivity(intent);
        });

        holder.buttonFavorite.setOnClickListener(v -> {
            // Hapus dari wishlist
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                recipeDao.delete(recipe);
                handler.post(() -> {
                    wishlist.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, wishlist.size());
                    Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRecipe;
        TextView textViewTitle;
        ImageButton buttonFavorite;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            textViewTitle = itemView.findViewById(R.id.textViewRecipeTitle);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }
    }
}