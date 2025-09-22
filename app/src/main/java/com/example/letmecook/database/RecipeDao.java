package com.example.letmecook.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WishlistRecipe recipe);

    @Delete
    void delete(WishlistRecipe recipe);

    @Query("SELECT * FROM wishlist_recipes ORDER BY id DESC")
    List<WishlistRecipe> getAllWishlistRecipes();

    @Query("SELECT * FROM wishlist_recipes WHERE recipe_id = :recipeId LIMIT 1")
    WishlistRecipe getWishlistRecipeById(int recipeId);
}