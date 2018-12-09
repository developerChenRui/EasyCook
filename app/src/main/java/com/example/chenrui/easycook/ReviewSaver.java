package com.example.chenrui.easycook;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;

public class ReviewSaver {

    private Recipe recipe;
    private JSONArray reviews = new JSONArray();
    private float averageReview;
    private int numReviewers;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    /*
    TODO:Recipe structure:
        Reviewer name
        Reviewer profile image
        Review text
        Number of stars
        List of usernames of users who liked the comment
        Timestamp

     */


    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    // Get just the average number of stars and the number of reviews on a stored recipe
    public void setReviewStats(final ReviewCallback callback) {
        if (recipe == null) {
            return;
        } else {
            DatabaseReference rRef = database.getReference("reviews/"+recipe.getRecipeId());
            rRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                         Object stuff = dataSnapshot.getValue();
                         if (stuff == null){
                             return;
                         }
                        JSONObject data = new JSONObject((HashMap)stuff);
                        if (data == null){
                            return;
                        }
                        averageReview = (float)data.getDouble("averageReview");
                        numReviewers = data.getInt("numReviewers");
                        recipe.setNumOfReviewer(numReviewers);
                        callback.onCallBack();
                    } catch (JSONException e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public float getAverageReview() {
        return averageReview;
    }

    public int getNumReviewers() {
        return numReviewers;
    }

    // Get just the average number of stars and the number of reviews on a new recipe
    public void setReviewStats(String recipeId, final ReviewCallback callback) {
        this.recipe = new Recipe();
        this.recipe.setRecipeId(recipeId);
        setReviewStats(callback);
    }

    // Adds a review to the database and cloud storage. For path, pass in getBaseContext().getFilesDir()
    public void addReview(final JSONObject review, final File path) {
        try{

            // Calculate new average review and number of reviewers
            averageReview = (float) ((averageReview * numReviewers) + review.getDouble("rating"))/(numReviewers+1);
            numReviewers++;

            // Add data to realtime database
            System.out.format("ADDREVIEW: recipeID: %s%n",recipe.getRecipeId());
            DatabaseReference rRef = database.getReference("reviews/"+recipe.getRecipeId());
            rRef.child("averageReview").setValue(averageReview);
            rRef.child("numReviewers").setValue(numReviewers);

            // Update reviews in cloud storage
            final StorageReference reviewRef = FirebaseStorage.getInstance().getReference().child("reviews/" + recipe.getRecipeId());
            try {
                final File localFile = File.createTempFile("reviews", "json");
                final Task<File> out = reviewRef.getFile(localFile).continueWith(new Continuation<FileDownloadTask.TaskSnapshot, File>() {
                    @Override
                    public File then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {
                        Log.d("ADDREVIEW","Got task");
                        return localFile;
                    }
                });

                out.addOnSuccessListener(new OnSuccessListener<File>() {

                    // File successfully downloaded
                    @Override
                    public void onSuccess(File file) {
                        Log.d("ADDREVIEW","Found file");
                        // read in file
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line = reader.readLine();
                            if (line == null) {
                                // Make new file
                                File reviewFile = new File(path,recipe.getRecipeId());

                                // Write reviews to file
                                FileWriter writer = new FileWriter(reviewFile,false);
                                JSONArray reviews = new JSONArray();
                                reviews.put(review);
                                writer.write(reviews.toString());
                                writer.close();

                                // Push file to cloud
                                Uri recipeURI = Uri.fromFile(reviewFile);
                                reviewRef.putFile(recipeURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        System.out.format("Successfully uploaded new review for %s%n",recipe.getRecipeId());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.format("Failed to upload review");
                                    }
                                });
                            } else {
                                do {
                                    try {
                                        // Add review to the JSONArray of reviews
                                        reviews = new JSONArray(line);
                                        reviews.put(review);
                                        try {
                                            // Write stored reviews to file and push back to cloud
                                            FileWriter writer = new FileWriter(file, false);
                                            writer.write(reviews.toString());
                                            writer.close();
                                            Uri recipeURI = Uri.fromFile(file);
                                            reviewRef.putFile(recipeURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    System.out.format("Successfully reuploaded review for %s%n", recipe.getRecipeId());
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        } catch (IOException e) {

                                        }
                                    } catch (JSONException e) {

                                    }
                                } while ((line = reader.readLine()) != null);
                            }
                        } catch (IOException e) {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {


                    // File doesn't exist so create a new one
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("REVIEWSAVER: File does not exist yet");

                        try {

                            // Make new file
                            File reviewFile = new File(path,recipe.getRecipeId());

                            // Write reviews to file
                            FileWriter writer = new FileWriter(reviewFile,false);
                            JSONArray reviews = new JSONArray();
                            reviews.put(review);
                            writer.write(reviews.toString());
                            writer.close();

                            // Push file to cloud
                            Uri recipeURI = Uri.fromFile(path);
                            reviewRef.putFile(recipeURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    System.out.format("Successfully uploaded review for %s%n",recipe.getRecipeName());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        } catch (IOException f) {

                        }
                    }
                });

            } catch (IOException e) {

            }


        } catch (JSONException e) {

        }
    }

    public void addReview(String recipeID, final JSONObject review, final File path) {
        recipe = new Recipe();
        recipe.setRecipeId(recipeID);
        addReview(review,path);
    }

    // Store reviews from the cloud
    public void fetchReviews(final ReviewCallback callback) {
        final StorageReference reviewRef = FirebaseStorage.getInstance().getReference().child("reviews/" + recipe.getRecipeId());
        try {

            // Get file from cloud
            final File localFile = File.createTempFile("reviews", "json");
            final Task<File> out = reviewRef.getFile(localFile).continueWith(new Continuation<FileDownloadTask.TaskSnapshot, File>() {
                @Override
                public File then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {
                    return localFile;
                }
            });
            out.addOnSuccessListener(new OnSuccessListener<File>() {

                // Successfully downloaded review file
                @Override
                public void onSuccess(File file) {
                    System.out.println("Fetched review file");
                    // Read in reviews and store
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {
                                reviews = new JSONArray(line);

                                System.out.format("Got reviews: %s%n", reviews);
                                callback.onCallBack();
                            } catch (JSONException e) {

                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to read reviews file");
                        callback.onCallBack();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                // No review file made yet
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Failed to get reviews file");
                    callback.onCallBack();
                }
            });
        } catch (IOException e) {

        }
    }

    public void fetchReviews(String recipeID, final ReviewCallback callback){
        this.recipe = new Recipe();
        this.recipe.setRecipeId(recipeID);
        this.fetchReviews(callback);
    }

    public JSONArray getReviews() {
        return reviews;
    }

}
