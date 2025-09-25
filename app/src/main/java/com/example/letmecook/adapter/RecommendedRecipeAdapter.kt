package com.example.letmecook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letmecook.databinding.ItemRecipeCardRecommendedBinding
import com.example.letmecook.model.Recipe
import com.example.letmecook.ui.RecipeDetailActivity
import java.text.DecimalFormat

class RecommendedRecipeAdapter(private var recipes: List<Recipe>) :
    RecyclerView.Adapter<RecommendedRecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: ItemRecipeCardRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            // Bind data yang sudah ada (judul dan gambar)
            binding.textViewRecipeTitle.text = recipe.title
            Glide.with(itemView.context)
                .load(recipe.image)
                .into(binding.imageViewRecipe)

            // LOGIKA BARU UNTUK MENAMPILKAN RATING
            // 1. Konversi skor 0-100 menjadi rating 0-5.0
            val ratingValue = recipe.spoonacularScore / 20.0
            val df = DecimalFormat("#.#")
            val formattedRating = df.format(ratingValue)

            // 2. Format jumlah rating/likes
            val likes = if (recipe.aggregateLikes > 1000) {
                "${recipe.aggregateLikes / 1000}rb+ rating"
            } else {
                "${recipe.aggregateLikes} rating"
            }

            // 3. Set teks ke TextView
            binding.textViewRating.text = "$formattedRating • $likes"


            // Listener untuk klik (tidak berubah)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, RecipeDetailActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeCardRecommendedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        this.recipes = newRecipes
        notifyDataSetChanged()
    }
}