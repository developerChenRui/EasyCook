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


    public void pushImage(File path, final ImageCallback callback){
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (image == null) {
            return;
        }
        System.out.format("IMAGESAVER: Got image %s%n",imageName );
        File imageFile = new File(path, imageName);
        System.out.format("IMAGESAVER: Made file %s%n",imageFile);
        try {
            System.out.format("IMAGESAVER: Trying file %s%n",imageFile);

            FileOutputStream fOut = new FileOutputStream(imageFile);
            System.out.format("IMAGESAVER: Made fOut %s%n",fOut);

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
            System.err.format("IMAGESAVER IO error %s%n", e);
        }

    }

    public void pushImage(String imageName, Bitmap image, File path, ImageCallback callback) {
        this.imageName = imageName;
        this.image = image;
        pushImage(path,callback);
    }



}
