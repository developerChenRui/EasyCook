package com.example.chenrui.easycook;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String recipeName;
    private String briefDescription;
    private float rating;
    private String recipeImageURL;
    private String profileURL;
    private String makerName;
    private String cookTime;
    private int numOfReviewer;
    private ArrayList<String> ingredients;
    private ArrayList<String> instructions;

    private String recipeId;

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getRecipeImageURL() {
        return recipeImageURL;
    }

    public void setRecipeImageURL(String recipeImageURL) {
        this.recipeImageURL = recipeImageURL;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }


    public Recipe(String recipeName,ArrayList<String> ingredients,String recipeImageURL,float rating ,String recipeId) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.rating = rating;
        this.recipeId = recipeId;
        this.recipeImageURL = recipeImageURL;
    }
    public Recipe() {

    }

    public Recipe(String recipeName, String briefDescription, float rating, String recipeImageURL, String profileURL, String makerName,
                  String cookTime, int numOfReviewer, ArrayList<String> ingredients, ArrayList<String> instructions, String recipeId) {
        this.recipeName = recipeName;
        this.briefDescription = briefDescription;
        this.rating = rating;
        this.recipeImageURL = recipeImageURL;
        this.profileURL = profileURL;
        this.makerName = makerName;
        this.cookTime = cookTime;
        this.numOfReviewer = numOfReviewer;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.recipeId = recipeId;
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
