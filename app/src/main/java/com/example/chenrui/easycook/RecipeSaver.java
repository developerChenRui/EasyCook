package com.example.chenrui.easycook;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RecipeSaver {
    private StorageReference recipeRef;
    private DatabaseReference databaseRef;
    private JSONObject recipeJSON;
    private Recipe recipe;
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    private JSONArray recipeList;




    // Get the recipe file from cloud storage and put in JSONArray
    public void fetchRecipe(String filename, final RecipeCallback callback){
        System.out.format("Getting %s%n",filename);
        this.recipeRef = FirebaseStorage.getInstance().getReference().child("recipes/" + filename);
        System.out.format("Got %s%n",filename);

        try {
            final File localFile = File.createTempFile("recipes", "json");
            final Task<File> out = recipeRef.getFile(localFile).continueWith(new Continuation<FileDownloadTask.TaskSnapshot, File>() {
                @Override
                public File then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {
                    return localFile;
                }
            });
            System.out.println("Got task");
            out.addOnSuccessListener(new OnSuccessListener<File>() {
                @Override
                public void onSuccess(File file) {
                    System.out.println("Got file");
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {
                                recipeJSON = new JSONObject(line);
                                recipe = new Recipe(
                                        recipeJSON.getString("name"),
                                        recipeJSON.getString("description"),
                                        (float) recipeJSON.getDouble("rating"),
                                        recipeJSON.getString("imageURL"),
                                        recipeJSON.getString("profileURL"),
                                        recipeJSON.getString("makerName"),
                                        recipeJSON.getInt("cookTime"),
                                        recipeJSON.getInt("numReviewers"),
                                        recipeJSON.getJSONArray("ingredients"),
                                        recipeJSON.getJSONArray("instructions"),
                                        recipeJSON.getJSONArray("tags"),
                                        recipeJSON.getString("recipeID")
                                );
                                JSONArray out = new JSONArray();
                                out.put(recipe);
                                callback.onCallBack(out);

                                System.out.format("Got stuff %s%n",line);
                            } catch (JSONException e) {
                                System.err.format("JSON File error %s%n",e);
                            }
                        }
                    } catch (IOException x) {
                        System.err.format("IOException: %s%n", x);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.err.format("Download exception: %s$n",e);

                }
            });

        } catch (IOException e) {
            System.err.format("IO exception: %s$n",e);
        }
    }


    public Recipe getRecipe() {
        return recipe;
    }

    public JSONObject getRecipeJSON() {
        return recipeJSON;
    }

    public void setRecipe(String recipeName, JSONArray ingredients, String imageURL, float rating, String recipeID) {
        recipe = new Recipe(recipeName, ingredients, imageURL, rating, recipeID);
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        try{
            this.recipeJSON = new JSONObject();
            this.recipeJSON.put("name",recipe.getRecipeName());
            this.recipeJSON.put("description",recipe.getBriefDescription());
            this.recipeJSON.put("rating",recipe.getRating());
            this.recipeJSON.put("imageURL",recipe.getRecipeImageURL());
            this.recipeJSON.put("profileURL",recipe.getProfileURL());
            this.recipeJSON.put("makerName",recipe.getMakerName());
            this.recipeJSON.put("cookTime",recipe.getCookTime());
            this.recipeJSON.put("numReviewers",recipe.getNumOfReviewer());
            this.recipeJSON.put("ingredients",recipe.getIngredients());
            this.recipeJSON.put("instructions",recipe.getInstructions());
            this.recipeJSON.put("tags",recipe.getTags());
            this.recipeJSON.put("recipeID",recipe.getRecipeId());
        } catch (JSONException e) {
            System.out.format("RECIPESAVER makeRecipe JSON error %s%n",e);
        }
    }

    // Push recipes into cloud storage
    public void pushRecipe(File path) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (recipeJSON == null){
            System.out.println("recipe is null");
            Log.d("RECIPESAVER PUSH","recipe is null");
            return;
        }
        System.out.format("RECIPESAVER PUSH %s%n",recipeJSON.toString());
        Log.d("RECIPESAVER PUSH", recipeJSON.toString());

        // Write recipe to file
        String recipeName = getFileName();
        File recipeFile = new File(path,recipeName);
        try {
            FileWriter writer = new FileWriter(recipeFile);
            writer.write(recipeJSON.toString());
            writer.close();

            // Push written file to cloud storage
            Uri recipeURI = Uri.fromFile(recipeFile);
            this.recipeRef = mStorageRef.child("recipes/" + recipeName);
            this.databaseRef = FirebaseDatabase.getInstance().getReference("recipes/"+recipeName);

            recipeRef.putFile(recipeURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("RECIPESAVER","File uploaded successfully");
                    databaseRef.setValue(recipe.getFlatTags());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("RECIPESAVER","File upload failure");
                }
            });
        } catch (IOException e) {
            System.err.format("Error opening file: %s%n",e);
        }

    }

    // Create a new recipe and store as both Recipe object and JSONObject
    public void makeRecipe(String recipeName, String briefDescription, float rating, String recipeImageURL, String profileURL, String makerName,
                           int cookTime, int numOfReviewer, JSONArray ingredients, JSONArray instructions, JSONArray tags, String recipeId) {
        this.recipe = new Recipe(recipeName,briefDescription,rating,recipeImageURL,profileURL,makerName,cookTime,numOfReviewer,ingredients,instructions,tags,recipeId);
        try{
            this.recipeJSON = new JSONObject();
            this.recipeJSON.put("name",recipeName);
            this.recipeJSON.put("description",briefDescription);
            this.recipeJSON.put("rating",rating);
            this.recipeJSON.put("imageURL",recipeImageURL);
            this.recipeJSON.put("profileURL",profileURL);
            this.recipeJSON.put("makerName",makerName);
            this.recipeJSON.put("cookTime",cookTime);
            this.recipeJSON.put("numReviewers",numOfReviewer);
            this.recipeJSON.put("ingredients",ingredients);
            this.recipeJSON.put("instructions",instructions);
            this.recipeJSON.put("tags",tags);
            this.recipeJSON.put("recipeID",recipeId);
        } catch (JSONException e) {
            System.out.format("RECIPESAVER makeRecipe JSON error %s%n",e);
        }
    }

    // Standardized method for getting a Recipe filename
    public String getFileName() {
        return recipe.getRecipeName().replace(' ','_') + "-" + recipe.getMakerName().replace(' ','_') + "-" + recipe.getRecipeId().replace(' ','_');
    }

    // Search for recipes with search terms. Get JSONArray of Recipe objects through the callback
    public void searchRecipes(String search, final RecipeCallback callback) {

        // Call Firebase Function to get filenames of recipes that match the search query
        Map<String,String> data = new HashMap<>();
        data.put("text",search);
        final Task<String> out = mFunctions.getHttpsCallable("searchRecipes").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result = (String) task.getResult().getData();
                return result;
            }
        });

        // Got task back that promises the files
        out.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                System.out.format("Received %s%n",s);
                try {

                    // Convert results string into a json array of filenames
                    final JSONArray result = new JSONArray(out.getResult());

                    // Reset the recipeList so that previous queries don't interfere
                    recipeList = new JSONArray();
                    if (result.length() == 0){
                        callback.onCallBack(recipeList);
                        return;
                    }

                    // Get each recipe with the callback and send own callback once all recipes have been received
                    for (int i = 0; i < result.length(); i++) {
                        fetchRecipe(result.getString(i), new RecipeCallback() {
                            @Override
                            public void onCallBack(JSONArray value) {
                                try {
                                    recipeList.put(value.get(0));
                                    System.out.format("Added %s%n", value.get(0).toString());

                                    // Make sure all recipes have been received
                                    // DANGEROUS CONCURRENCY
                                    if (recipeList.length() == result.length()) {
                                        callback.onCallBack(recipeList);
                                    }
                                }catch(JSONException e) {

                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    System.err.format("JSON error, reading in file %s%n",e);
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }



}
