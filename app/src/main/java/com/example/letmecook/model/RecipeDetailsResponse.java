package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecipeDetailsResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("summary")
    private String summary;
    @SerializedName("extendedIngredients")
    private List<ExtendedIngredient> extendedIngredients;
    @SerializedName("analyzedInstructions")
    private List<AnalyzedInstruction> analyzedInstructions;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getSummary() { return summary; }
    public List<ExtendedIngredient> getExtendedIngredients() { return extendedIngredients; }
    public List<AnalyzedInstruction> getAnalyzedInstructions() { return analyzedInstructions; }
}