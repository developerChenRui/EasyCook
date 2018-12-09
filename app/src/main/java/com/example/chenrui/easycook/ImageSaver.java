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

public class ImageSaver {

    private StorageReference imageRef;
    private String imageName;
    private Bitmap image;
    private String imageURL;

    /***
     * 
     * Constructors
     *
     ***/
    public ImageSaver(){ }

    public ImageSaver(String imageName, Bitmap image) {
        this.imageName = imageName;
        this.image = image;
    }


    /***
     *
     * Name: setImage
     * @param imageName  String  Filename of image
     * @param image      Bitmap  Image object
     *
     * Description: Sets the image name and image
     */
    public void setImage(String imageName, Bitmap image) {
        this.imageName = imageName;
        this.image = image;
    }

//    public void fetchImage(String imgURL, final ImageCallback callback) {
//        Target image;
//        image = new Target() {
//
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                callback()
//            }
//
//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };
//        Picasso.get().load(imgURL).into(image);
//
//    }


    /***
     *
     * Name: pushImage
     * @param path         File           getBaseContext().getFilesDir()
     * @param callback     ImageCallback  callback to get the image URL from firebase
     *
     * Description: Pushes the stored image to cloud storage. Gets back URL string from callback
     ***/

    public void pushImage(File path, final ImageCallback callback){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (image == null) {
            return;
        }
        File imageFile = new File(path, imageName);
        try {
            FileOutputStream fOut = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG,90,fOut);
            fOut.flush();
            fOut.close();
            Uri imageURI = Uri.fromFile(imageFile);
            imageRef = mStorageRef.child("images/"+imageName);
            imageRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.format("URL format %s%n",uri.toString());
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

        }

    }

    /***
     *
     * Name: pushImage
     * @param imageName    String         Filename of the image
     * @param image        Bitmap         image object
     * @param path         File           getBaseContext().getFilesDir()
     * @param callback     ImageCallback  callback to get the image URL from firebase
     */
    public void pushImage(String imageName, Bitmap image, File path, final ImageCallback callback) {
        this.imageName = imageName;
        this.image = image;
        pushImage(path,callback);
    }

}
