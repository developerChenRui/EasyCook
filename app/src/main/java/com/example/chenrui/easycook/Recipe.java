package com.example.chenrui.easycook;

import android.graphics.Bitmap;

import java.util.List;

public class Recipe {
    private String recipeName;
    private String briefDescription;
    private float rating;
    private Bitmap recipeImage;

    private Bitmap profile;
    private String makerName;
    private List<String> ingredients;
    private List<String> instructions;

    public Recipe() {

    }

    public Recipe(String recipeName, String briefDescription, float rating, Bitmap recipeImage, Bitmap profile, String makerName,
    List<String> ingredients, List<String> instructions) {
        this.recipeName = recipeName;
        this.briefDescription = briefDescription;
        this.rating = rating;
        this.recipeImage = recipeImage;
        this.profile = profile;
        this.makerName = makerName;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Bitmap getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(Bitmap recipeImage) {
        this.recipeImage = recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

}
