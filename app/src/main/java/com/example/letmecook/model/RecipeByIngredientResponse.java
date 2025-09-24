package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecipeByIngredientResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("imageType")
    private String imageType;
    @SerializedName("usedIngredientCount")
    private int usedIngredientCount;
    @SerializedName("missedIngredientCount")
    private int missedIngredientCount;
    @SerializedName("missedIngredients")
    private List<MissedIngredient> missedIngredients;
    @SerializedName("usedIngredients")
    private List<UsedIngredient> usedIngredients;
    @SerializedName("unusedIngredients")
    private List<UnusedIngredient> unusedIngredients;
    @SerializedName("likes")
    private int likes;

    // Tambahkan constructor ini jika belum ada, untuk kompatibilitas
    public RecipeByIngredientResponse(int id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    // --- Getter dan Setter lainnya ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public int getUsedIngredientCount() { return usedIngredientCount; }
    public void setUsedIngredientCount(int usedIngredientCount) { this.usedIngredientCount = usedIngredientCount; }
    public int getMissedIngredientCount() { return missedIngredientCount; }
    public void setMissedIngredientCount(int missedIngredientCount) { this.missedIngredientCount = missedIngredientCount; }
    public List<MissedIngredient> getMissedIngredients() { return missedIngredients; }
    public void setMissedIngredients(List<MissedIngredient> missedIngredients) { this.missedIngredients = missedIngredients; }
    public List<UsedIngredient> getUsedIngredients() { return usedIngredients; }
    public void setUsedIngredients(List<UsedIngredient> usedIngredients) { this.usedIngredients = usedIngredients; }
    public List<UnusedIngredient> getUnusedIngredients() { return unusedIngredients; }
    public void setUnusedIngredients(List<UnusedIngredient> unusedIngredients) { this.unusedIngredients = unusedIngredients; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    // Nested classes (MissedIngredient, UsedIngredient, UnusedIngredient)
    public static class MissedIngredient { /* ... */ }
    public static class UsedIngredient { /* ... */ }
    public static class UnusedIngredient { /* ... */ }
}