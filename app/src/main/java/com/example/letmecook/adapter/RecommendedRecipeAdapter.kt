package com.example.letmecook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letmecook.databinding.ItemRecipeCardRecommendedBinding
import com.example.letmecook.model.Recipe
import com.example.letmecook.ui.RecipeDetailActivity

class RecommendedRecipeAdapter(private var recipes: List<Recipe>) :
    RecyclerView.Adapter<RecommendedRecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: ItemRecipeCardRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.textViewRecipeTitle.text = recipe.title
            Glide.with(itemView.context)
                .load(recipe.image)
                .into(binding.imageViewRecipe)

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