package com.example.chenrui.easycook;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String recipeName;
    private String briefDescription;
    private float rating;
    private Bitmap recipeImage;

    private Bitmap profile;
    private String makerName;
    private String cookTime;
    private int numOfReviewer;
    private ArrayList<String> ingredients;
    private ArrayList<String> instructions;

    public Recipe() {

    }

    public Recipe(String recipeName, String briefDescription, float rating, Bitmap recipeImage, Bitmap profile, String makerName,
                  String cookTime, int numOfReviewer, ArrayList<String> ingredients, ArrayList<String> instructions) {
        this.recipeName = recipeName;
        this.briefDescription = briefDescription;
        this.rating = rating;
        this.recipeImage = recipeImage;
        this.profile = profile;
        this.makerName = makerName;
        this.cookTime = cookTime;
        this.numOfReviewer = numOfReviewer;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }


    public int getNumOfReviewer() {
        return numOfReviewer;
    }

    public void setNumOfReviewer(int numOfReviewer) {
        this.numOfReviewer = numOfReviewer;
    }


    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
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

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

}
