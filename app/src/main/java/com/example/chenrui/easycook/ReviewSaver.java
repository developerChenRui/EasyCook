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


/***
 * ReviewSaver
 *
 * All Review related Firebase interactions should be done through the Review Saver
 */
public class ReviewSaver {

    private Recipe recipe;
    private JSONArray reviews = new JSONArray();
    private float averageReview;
    private int numReviewers;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();




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
                             averageReview = 0.0f;
                             numReviewers = 0;
                             recipe.setNumOfReviewer(0);
                             recipe.setRating(0.0f);
                             callback.onCallBack();
                             return;
                         }
                        JSONObject data = new JSONObject((HashMap)stuff);

                        averageReview = (float)data.getDouble("averageReview");
                        numReviewers = data.getInt("numReviewers");
                        recipe.setNumOfReviewer(numReviewers);
                        recipe.setRating(averageReview);
                        callback.onCallBack();
                    } catch (JSONException e) {
                        averageReview = 0.0f;
                        numReviewers = 0;
                        recipe.setNumOfReviewer(0);
                        recipe.setRating(0.0f);
                        callback.onCallBack();
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

    /***
     * addReview
     *
     * @param review     final JSONObject  The review information
     * @param path       final File        Should always be getBaseContext().getFilesDir()
     *
     * Adds a review to the stored recipe's reviews file. If it doesn't exist yet then create a new file
     ***/
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
                                reviewFile.delete();
                            } else {
                                do {
                                    try {
                                        // Add review to the JSONArray of reviews
                                        reviews = new JSONArray(line);
                                        boolean found = false;
                                        for (int i = 0; i < reviews.length(); i++){
                                            if (reviews.getJSONObject(i).getString("email").equals(review.getString("email"))) {

                                                reviews.remove(i);
                                                break;
                                            }
                                        }




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

                            reviewFile.delete();

                        } catch (IOException f) {

                        }
                    }
                });

            } catch (IOException e) {

            }


        } catch (JSONException e) {

        }
    }

    /***
     * addReview
     *
     * @param recipeID     String            Recipe ID that the review pertains to
     * @param review       final JSONObject  The review information
     * @param path         final File        Should always be getBaseContext().getFilesDir()
     *
     * Adds a review to a recipe's reviews file. If it doesn't exist yet then create a new file
     */
    public void addReview(String recipeID, final JSONObject review, final File path) {
        recipe = new Recipe();
        recipe.setRecipeId(recipeID);
        addReview(review,path);
    }


    /***
     * fetchReviews
     *
     * @param callback      ReviewCallback  Get list of reviews
     *
     * Get list of reviews through the callback
     ***/
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


    /***
     * fetchReviews
     *
     * @param recipeID      String          Recipe ID
     * @param callback      ReviewCallback  Get list of reviews
     *
     * Get list of reviews through the callback
     ***/
    public void fetchReviews(String recipeID, final ReviewCallback callback){
        this.recipe = new Recipe();
        this.recipe.setRecipeId(recipeID);
        this.fetchReviews(callback);
    }


    /***
     * updateReview
     *
     * @param recipeID     String  RecipeID
     * @param review       Review  Review to update
     *
     * Update the information of a review. Usually if a user likes a recipe
     ***/
    public void updateReview(String recipeID, Review review) {
        final StorageReference reviewRef = FirebaseStorage.getInstance().getReference().child("reviews/" + recipeID);
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
                @Override
                public void onSuccess(File file) {
                    // Read in reviews and store
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {
                                reviews = new JSONArray(line);
                                System.out.format("Got reviews: %s%n", reviews);

                                for (int i = 0; i < reviews.length(); i++) {
                                    if (((Review)reviews.get(i)).getEmail().equals(review.getEmail())) {
                                        reviews.put(i,review);
                                        break;
                                    }
                                }

                                FileWriter writer = new FileWriter(file);
                                writer.write(reviews.toString());
                                writer.close();

                                Uri reviewURI = Uri.fromFile(file);
                                reviewRef.putFile(reviewURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            } catch (JSONException e) {

                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to read reviews file");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {

        }
    }


    /***
     * ChangeUserLike
     *
     * @param recipeID          String   Recipe ID
     * @param reviewerEmail     String   Email of the reviewer
     * @param likerEmail        String   Email of the user who liked/unliked the review
     * @param isAdd             Boolean  Whether it is adding or removing a like
     *
     * Update a review's user likes
     */
    public void changeUserLike(String recipeID, String reviewerEmail, String likerEmail, Boolean isAdd) {
        final StorageReference reviewRef = FirebaseStorage.getInstance().getReference().child("reviews/" + recipeID);
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
                @Override
                public void onSuccess(File file) {
                    // Read in reviews and store
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {
                                reviews = new JSONArray(line);
                                System.out.format("Got reviews: %s%n", reviews);

                                for (int i = 0; i < reviews.length(); i++) {
                                    Review review = new Review();
                                    review.fromJSON(reviews.getJSONObject(i));
                                    if (review.getEmail().equals(reviewerEmail)) {
                                        if(isAdd){
                                            review.addUserLike(likerEmail);
                                        } else {
                                            review.removeUserLike(likerEmail);
                                        }
                                        break;
                                    }
                                }

                                FileWriter writer = new FileWriter(file);
                                writer.write(reviews.toString());
                                writer.close();

                                System.out.println("Wrote reviews to file");

                                Uri reviewURI = Uri.fromFile(file);
                                reviewRef.putFile(reviewURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            } catch (JSONException e) {

                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to read reviews file");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {

        }
    }

    public JSONArray getReviews() {
        return reviews;
    }

}
