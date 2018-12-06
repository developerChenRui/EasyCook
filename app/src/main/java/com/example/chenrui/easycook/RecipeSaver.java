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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RecipeSaver {
    private StorageReference recipeRef;
    private DatabaseReference databaseRef;
    private JSONObject recipeJSON;
    private Recipe recipe;



    public void fetchRecipe(String filename){
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

    // Push recipes into cloud storage
    public void pushRecipe(File path, StorageReference mStorageRef) {
        if (recipeJSON == null){
            System.out.println("recipe is null");
            Log.d("RECIPESAVER PUSH","recipe is null");
            return;
        }
        System.out.format("RECIPESAVER PUSH %s%n",recipeJSON.toString());
        Log.d("RECIPESAVER PUSH", recipeJSON.toString());

        String recipeName = getFileName();
        File recipeFile = new File(path,recipeName);
        try {
            FileWriter writer = new FileWriter(recipeFile);
            writer.write(recipeJSON.toString());
            writer.close();
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

    public String getFileName() {
        return recipe.getRecipeName().replace(' ','_') + "-" + recipe.getMakerName().replace(' ','_') + "-" + recipe.getRecipeId().replace(' ','_');
    }



}
