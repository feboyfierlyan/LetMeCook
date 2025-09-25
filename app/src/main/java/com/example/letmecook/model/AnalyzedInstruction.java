package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnalyzedInstruction {
    @SerializedName("steps")
    private List<Step> steps;

    public List<Step> getSteps() { return steps; }
}