package com.example.letmecook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.letmecook.R;
import com.example.letmecook.model.Recipe; // UBAH IMPORT INI
import com.example.letmecook.ui.RecipeDetailActivity;
import java.text.DecimalFormat;
import java.util.List;

// GANTI TIPE DATA LIST MENJADI Recipe
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private Context context;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
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

        // Gunakan getter dari data class Kotlin
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

        // Logika untuk tombol favorit tidak berubah
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // GANTI TIPE DATA LIST MENJADI Recipe
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
            // Inisialisasi tidak berubah
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            textViewTitle = itemView.findViewById(R.id.textViewRecipeTitle);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
            textViewCookingTime = itemView.findViewById(R.id.text_view_cooking_time);
            textViewRating = itemView.findViewById(R.id.text_view_rating);
        }
    }
}