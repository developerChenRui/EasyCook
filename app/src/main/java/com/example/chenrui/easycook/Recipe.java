package com.example.chenrui.easycook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 * Recipe
 *
 * Stores all relevant recipe information
 ***/
public class Recipe {
    private String recipeName = "";
    private String briefDescription = "";
    private float rating = 0.0f;
    private String recipeImageURL = "";
    private String profileURL = "";
    private String makerName = "";
    private int cookTime = 0;
    private int numOfReviewer = 0;
    private JSONArray ingredients = new JSONArray();
    private JSONArray instructions = new JSONArray();
    private JSONArray tags = new JSONArray();

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


    public Recipe(String recipeName,JSONArray ingredients,String recipeImageURL,float rating ,String recipeId) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.rating = rating;
        this.recipeId = recipeId;
        this.recipeImageURL = recipeImageURL;
    }
    public Recipe() {

    }

    public Recipe(String recipeName, String briefDescription, float rating, String recipeImageURL, String profileURL, String makerName,
                  int cookTime, int numOfReviewer, JSONArray ingredients, JSONArray instructions, JSONArray tags, String recipeId) {
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
        this.tags = tags;
        this.recipeId = recipeId;
    }


    public int getNumOfReviewer() {
        return numOfReviewer;
    }

    public void setNumOfReviewer(int numOfReviewer) {
        this.numOfReviewer = numOfReviewer;
    }


    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
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

    public JSONArray getIngredients() {
        return ingredients;
    }

    public void setIngredients(JSONArray ingredients) {
        this.ingredients = ingredients;
    }

    public JSONArray getInstructions() {
        return instructions;
    }

    public void setInstructions(JSONArray instructions) {
        this.instructions = instructions;
    }

    public void setTags(JSONArray tags) {
        this.tags = tags;
    }

    public JSONArray getTags() {
        return this.tags;
    }

    /***
     * getFlatTags
     *
     * @return     String  Flat representation of the tags
     *
     * Flattens tags for easy storage in the Realtime Database
     ***/
    public String getFlatTags() {
        StringBuilder sTags = new StringBuilder("");
        for (int i = 0; i < this.tags.length(); i++) {
            try {
                sTags.append(this.tags.getString(i));
                if (i < this.tags.length() - 1) {
                    sTags.append(", ");
                }

            } catch (JSONException e) {
                System.err.format("Tags not formatted properly: %s%n", e);
            }
        }
        return sTags.toString();
    }


    /***
     * addIngredients
     *
     * @param ingredient     String  Name of ingredient
     * @param amount         float   Amount of ingredient
     * @param measure        String  Unit
     *
     * Add ingredient to ingredients list
     ***/
    public void addIngredients(String ingredient, float amount, String measure) {
        try{
            JSONObject ing = new JSONObject();
            ing.put("name",ingredient);
            ing.put("amount",amount);
            ing.put("measure",measure);
            this.ingredients.put(ing);
        } catch (JSONException e) {
            //ERROR
        }
    }

    public void fromJSON(JSONObject jsonIn) {
        try {
            this.recipeName = jsonIn.getString("name");
            this.briefDescription = jsonIn.getString("briefDescription");
            this.rating = (float)jsonIn.getDouble("rating");
            this.recipeImageURL = jsonIn.getString("recipeImageURL");
            this.profileURL = jsonIn.getString("profileURL");
            this.makerName = jsonIn.getString("makerName");
            this.cookTime = jsonIn.getInt("cookTime");
            this.numOfReviewer = jsonIn.getInt("numOfReviewer");
            this.ingredients = jsonIn.getJSONArray("ingredients");
            this.instructions = jsonIn.getJSONArray("instructions");
            this.tags = jsonIn.getJSONArray("tags");
            this.recipeId = jsonIn.getString("recipeID");
        } catch (JSONException e) {

        }
    }

    public JSONObject toJSON() {
        JSONObject out = new JSONObject();
        try {
            out.put("name",this.recipeName);
            out.put("briefDescription",this.briefDescription);
            out.put("rating",this.rating);
            out.put("recipeImageURL",this.recipeImageURL);
            out.put("profileURL",this.profileURL);
            out.put("makerName",this.makerName);
            out.put("cookTime",this.cookTime);
            out.put("numOfReviewer",this.numOfReviewer);
            out.put("ingredients",this.ingredients);
            out.put("instructions",this.instructions);
            out.put("tags",this.tags);
            out.put("recipeID",this.recipeId);
        } catch (JSONException e) {

        }
        return out;
    }
}
