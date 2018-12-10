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

    public void fetchProfile(String email, final ProfileCallback callback) {
        email = email.replace('.','_').replace('@','_');
        this.profileRef = FirebaseStorage.getInstance().getReference().child("users/" + email);

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
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {
                                profileJSON = new JSONObject(line);
                                profile = new User();
                                profile.fromJSON(profileJSON);
                            } catch (JSONException e) {

                            }
                        }
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
    }

    public void checkProfile(File path, ProfileCallback callback) {
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
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line = reader.readLine();
                        if (line == null) {
                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                            callback.onCallback(new User());
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

    public void checkProfile(User profile, File path, ProfileCallback callback) {
        this.profile = profile;
        checkProfile(path, callback);
    }

    public void pushProfile(User profile, File path) {
        this.profile = profile;
        this.profileJSON = profile.toJSON();
        pushProfile(path);
    }

    public void updateProfile(User profile, File path) {
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
//                                  System.out.format("Successfully uploaded new review for %s%n", recipe.getRecipeId());
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
//                                  System.out.format("Failed to upload review");
                              }
                          });

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
