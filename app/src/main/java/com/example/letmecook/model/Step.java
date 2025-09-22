package com.example.letmecook.model;

import com.google.gson.annotations.SerializedName;

public class Step {
    @SerializedName("number")
    private int number;
    @SerializedName("step")
    private String step;

    public int getNumber() { return number; }
    public String getStep() { return step; }
}