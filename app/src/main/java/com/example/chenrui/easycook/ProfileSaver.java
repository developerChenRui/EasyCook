package com.example.chenrui.easycook;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/***
 * ProfileSaver
 *
 * All Firebase interactions should be done through the ProfileSaver
 ***/
public class ProfileSaver {

    private StorageReference profileRef;
    private User profile;
    private JSONObject profileJSON;

    public User getProfile(){
        return this.profile;
    }

    public JSONObject getProfileJSON() {
        return profileJSON;
    }

    public void setProfile(User profile) {
        this.profile = profile;
        this.profileJSON = profile.toJSON();
    }

    /***
     * fetchProfile
     *
     * @param email        String           Email of the user being searched
     * @param callback     ProfileCallback  Callback to notify the app that the profile has been received
     *
     * Gets the profile with associated email
     */
    public void fetchProfile(String email, final ProfileCallback callback) {
        email = email.replace('.','_').replace('@','_');
        this.profileRef = FirebaseStorage.getInstance().getReference().child("users/" + email);

        try {

            // Read file locally
            final File localFile = File.createTempFile("users","json");
            final Task<File> out = profileRef.getFile(localFile).continueWith(new Continuation<FileDownloadTask.TaskSnapshot, File>() {
                @Override
                public File then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {

                    return localFile;
                }
            });

            // Finished downloading profile file
            out.addOnSuccessListener(new OnSuccessListener<File>() {
                @Override
                public void onSuccess(File file) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {

                                // Store profile
                                profileJSON = new JSONObject(line);
                                profile = new User();
                                profile.fromJSON(profileJSON);
                            } catch (JSONException e) {

                            }
                        }

                        // Pass profile into callback
                        callback.onCallback(profile);
                    } catch (IOException e) {

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
     * pushProfile
     *
     * @param path     File  Should always be getBaseContext().getFilesDir()
     *
     * Pushes current stored profile to cloud storage
     */
    public void pushProfile(File path) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (this.profileJSON == null){
            return;
        }

        String email = this.profile.getCleanEmail();
        File profileFile = new File(path,email);
        try {
            FileWriter writer = new FileWriter(profileFile);
            writer.write(profileJSON.toString());
            writer.close();

            // Push written file to cloud storage
            Uri profileURI = Uri.fromFile(profileFile);
            this.profileRef = mStorageRef.child("users/"+email);

            profileRef.putFile(profileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("PROFILESAVER","File uploaded successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {

        }
        profileFile.delete();
    }

    /***
     * pushProfile
     *
     * @param profile     User  Profile that needs to be pushed
     * @param path        File  Should always be getBaseContext().getFilesDir()
     */
    public void pushProfile(User profile, File path) {
        this.profile = profile;
        this.profileJSON = profile.toJSON();
        pushProfile(path);
    }


    /***
     * checkProfile
     *
     * @param path         File             Should always be getBaseContext().getFilesDir()
     * @param callback     ProfileCallback  Callback to get a profile object back
     * @param makeNew      Boolean          If true, make a new profile if one doesn't exist
     *
     * Checks the existence of a profile. If none exist, output an empty profile through the callback
     * If makeNew is true, create a new profile if it doesn't exist.
     ***/
    public void checkProfile(File path, ProfileCallback callback, Boolean makeNew) {
        String email = this.profile.getCleanEmail();
        this.profileRef = FirebaseStorage.getInstance().getReference().child("users/"+email);
        try {
            final File localFile = File.createTempFile("users","json");
            final Task<File> out = profileRef.getFile(localFile).continueWith(new Continuation<FileDownloadTask.TaskSnapshot, File>() {
                @Override
                public File then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {
                    return localFile;
                }
            });
            out.addOnSuccessListener(new OnSuccessListener<File>() {
                @Override
                public void onSuccess(File file) {
                    System.out.format("Got profile file for %s%n", profile.getEmail());
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line = reader.readLine();


                        if (line == null) {
                            System.out.format("Profile doesn't exist for %s%n",profile.getEmail());
                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                            callback.onCallback(new User());
                            if (!makeNew) {
                                return;
                            }
                            if (profileJSON == null){
                                return;
                            }

                            String email = profile.getCleanEmail();
                            File profileFile = new File(path,email);
                            try {
                                FileWriter writer = new FileWriter(profileFile);
                                writer.write(profileJSON.toString());
                                writer.close();

                                // Push written file to cloud storage
                                Uri profileURI = Uri.fromFile(profileFile);
                                profileRef = mStorageRef.child("users/"+email);

                                profileRef.putFile(profileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.i("PROFILESAVER","File uploaded successfully");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            } catch (IOException e) {

                            }
                            profileFile.delete();
                        } else {
                            do {
                                try {
                                    profileJSON = new JSONObject(line);
                                    profile = new User();
                                    profile.fromJSON(profileJSON);
                                } catch (JSONException e) {

                                }
                                callback.onCallback(profile);
                            } while ((line = reader.readLine()) != null);
                        }
                    } catch (IOException e) {

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
     * checkProfile
     *
     * @param profile      User             User profile that should be checked
     * @param path         File             Should always be getBaseContext().getFilesDir()
     * @param callback     ProfileCallback  Callback to get a profile object back
     * @param makeNew      Boolean          If true, make a new profile if one doesn't exist
     *
     * Checks the existence of a profile. If none exist, output an empty profile through the callback
     * If makeNew is true, create a new profile if it doesn't exist. Calls checkProfile
     ***/
    public void checkProfile(User profile, File path, ProfileCallback callback, Boolean makeNew) {
        this.profile = profile;
        this.profileJSON = profile.toJSON();
        checkProfile(path, callback, makeNew);
    }


    /***
     * updateProfile
     *
     * @param profile     User  Profile that will be used to update information
     * @param path        File  Should always be getBaseContext().getFilesDir()
     *
     * Updates user information with the inputted profile
     */
    public void updateProfile(User profile, File path) {
        System.out.format("Utils.user: %s%n",Utils.user);
        final StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/"+profile.getCleanEmail());
        try {
            final File localFile = File.createTempFile("profile","json");
            final Task<File> out = profileRef.getFile(localFile).continueWith(new Continuation<FileDownloadTask.TaskSnapshot, File>() {
                @Override
                public File then(@NonNull Task<FileDownloadTask.TaskSnapshot> task) throws Exception {
                    return localFile;
                }
            });

            out.addOnSuccessListener(new OnSuccessListener<File>() {
              @Override
              public void onSuccess(File file) {
                  try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                      String line = reader.readLine();

                      // Profile doesn't exist? Should never get here
                      if (line == null) {

                          // Make new file
                          File profileFile = new File(path, profile.getCleanEmail());

                          FileWriter fileWriter = new FileWriter(profileFile);
                          fileWriter.write(profile.toJSON().toString());
                          fileWriter.close();

                          Uri profileURI = Uri.fromFile(profileFile);
                          profileRef.putFile(profileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                              }
                          });

                          profileFile.delete();

                      // Profile exists
                      } else {
                          FileWriter writer = new FileWriter(file, false);
                          writer.write(profile.toJSON().toString());
                          writer.close();

                          Uri profileURI = Uri.fromFile(file);
                          profileRef.putFile(profileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {

                              }
                          });

                      }
                  } catch (IOException e) {

                  }
              }
            });

        } catch (Exception e) {

        }
    }
}
