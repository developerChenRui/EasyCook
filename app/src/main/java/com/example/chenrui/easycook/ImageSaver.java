package com.example.chenrui.easycook;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/***
 * ImageSaver
 *
 * All interactions with the cloud storage involving images should be done through this class
 ***/
public class ImageSaver {

    private StorageReference imageRef;
    private String imageName;
    private Bitmap image;
    private String imageURL;


    /***
     * pushImage
     *
     * @param path         File           Should always be getBaseContext().getFilesDir()
     * @param callback     ImageCallback  Callback to get the image URL from the server
     *
     * Uploads the stored bitmap to cloud storage and returns a url through the callback
     */
    public void pushImage(File path, final ImageCallback callback){

        // Get the Cloud Storage reference
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (image == null) {
            return;
        }
        System.out.format("IMAGESAVER: Got image %s%n",imageName );
        File imageFile = new File(path, imageName);
        System.out.format("IMAGESAVER: Made file %s%n",imageFile);
        try {
            System.out.format("IMAGESAVER: Trying file %s%n",imageFile);

            // Write bitmap to file
            FileOutputStream fOut = new FileOutputStream(imageFile);
            System.out.format("IMAGESAVER: Made fOut %s%n",fOut);

            image.compress(Bitmap.CompressFormat.JPEG,90,fOut);
            fOut.flush();
            fOut.close();

            // Push file to cloud storage
            Uri imageURI = Uri.fromFile(imageFile);
            imageRef = mStorageRef.child("images/"+imageName);
            imageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.format("URL format %s%n",uri.toString());

                            // Pass the image's url to the callback
                            callback.onCallback(uri.toString());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            System.err.format("IMAGESAVER IO error %s%n", e);
        }

    }


    /***
     * pushImage
     *
     * @param imageName    String         Name for the image file
     * @param image        Bitmap         The image
     * @param path         File           Should always be getBaseContext().getFilesDir()
     * @param callback     ImageCallback  Callback to get the url of the string
     *
     * Stores the image and file name and calls pushImage to upload to cloud storage
     */
    public void pushImage(String imageName, Bitmap image, File path, ImageCallback callback) {
        this.imageName = imageName;
        this.image = image;
        pushImage(path,callback);
    }


    /***
     * pushImage
     *
     * @param imageName    String         Name for the image file
     * @param image        Bitmap         The image
     * @param path         File           Should always be getBaseContext().getFilesDir()
     * @param i            int            The number of the step that the image relates to
     * @param callback     ImageCallback  Callback to get the url of the string
     *
     * Pushes images for an instruction. Needs to be able to return the associated index of the image.
     */
    public void pushImage(String imageName, Bitmap image, File path, int i, InstructionsCallback callback) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (image == null) {
            return;
        }
        System.out.format("IMAGESAVER: Got image %s%n",imageName );
        File imageFile = new File(path, imageName);
        System.out.format("IMAGESAVER: Made file %s%n",imageFile);
        try {
            System.out.format("IMAGESAVER: Trying file %s%n",imageFile);


            // Write image to a file
            FileOutputStream fOut = new FileOutputStream(imageFile);
            System.out.format("IMAGESAVER: Made fOut %s%n",fOut);

            image.compress(Bitmap.CompressFormat.JPEG,90,fOut);
            fOut.flush();
            fOut.close();

            // Upload image file to cloud storage
            Uri imageURI = Uri.fromFile(imageFile);
            imageRef = mStorageRef.child("images/"+imageName);
            imageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.format("URL format %s%n",uri.toString());

                            // Pass image url and index to callback
                            callback.onCallback(uri.toString(), i);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            System.err.format("IMAGESAVER IO error %s%n", e);
        }
        imageFile.delete();
    }



}
