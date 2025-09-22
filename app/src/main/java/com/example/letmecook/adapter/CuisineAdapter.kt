package com.example.letmecook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letmecook.databinding.ItemCuisineBinding
import com.example.letmecook.model.Cuisine

class CuisineAdapter(
    private val cuisines: List<Cuisine>,
    private val onItemClick: (Cuisine) -> Unit
) : RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>() {

    inner class CuisineViewHolder(private val binding: ItemCuisineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cuisine: Cuisine) {
            binding.cuisineName.text = cuisine.name
            Glide.with(itemView.context)
                .load(cuisine.imageResId)
                .into(binding.cuisineImage)

            itemView.setOnClickListener {
                onItemClick(cuisine)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val binding = ItemCuisineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuisineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        holder.bind(cuisines[position])
    }

    override fun getItemCount(): Int = cuisines.size
}