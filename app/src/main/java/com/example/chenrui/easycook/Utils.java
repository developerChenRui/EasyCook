package com.example.chenrui.easycook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;


public class Utils {
    public static String username= null;
    public static String md5Encryption(final String input){
        String result = "";
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input.getBytes(Charset.forName("UTF8")));
            byte[] resultByte = messageDigest.digest();
            result = new String(Hex.encodeHex(resultByte));
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Transform time unit to different time format
     * @param millis time stamp
     * @return formatted string of time stamp
     */
    public static String timeTransformer(long millis) {
        long currenttime = System.currentTimeMillis();
        long diff = currenttime - millis;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else {
            return days + " days ago";
        }
    }

    /**
     * Download an Image from the given URL, then decodes and returns a Bitmap object.
     */
    public static Bitmap getBitmapFromURL(String imageUrl) {
        Bitmap bitmap = null;

        if (bitmap == null) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage().toString());
            }
        }

        return bitmap;
    }

    public static int distanceBetweenTwoLocations(double currentLatitude,
                                                  double currentLongitude,
                                                  double destLatitude,
                                                  double destLongitude) {

        Location currentLocation = new Location("CurrentLocation");
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);
        Location destLocation = new Location("DestLocation");
        destLocation.setLatitude(destLatitude);
        destLocation.setLongitude(destLongitude);
        double distance = currentLocation.distanceTo(destLocation);

        double inches = (39.370078 * distance);
        int miles = (int) (inches / 63360);
        return miles;
    }


    public static Bundle Recipe2Bundle(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putString("recipeName",recipe.getRecipeName());
        bundle.putString("briefDescription",recipe.getBriefDescription());
        bundle.putFloat("rating",recipe.getRating());
        bundle.putParcelable("recipeImage",recipe.getRecipeImage());
        bundle.putParcelable("profile",recipe.getProfile());
        bundle.putString("makerName",recipe.getMakerName());
        bundle.putStringArrayList("ingredients",recipe.getIngredients());
        bundle.putStringArrayList("instructions",recipe.getInstructions());
        bundle.putInt("numOfReviewer",recipe.getNumOfReviewer());
        bundle.putString("cookTime",recipe.getCookTime());
        return bundle;
    }

    public static Recipe Bundle2Recipe(Bundle bundle) {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(bundle.getString("recipeName"));
        recipe.setBriefDescription(bundle.getString("briefDescription"));
        recipe.setRating(bundle.getFloat("rating"));
        recipe.setRecipeImage(bundle.getParcelable("recipeImage"));
        recipe.setProfile(bundle.getParcelable("profile"));
        recipe.setMakerName(bundle.getString("makerName",recipe.getMakerName()));
        recipe.setIngredients(bundle.getStringArrayList("ingredients"));
        recipe.setInstructions(bundle.getStringArrayList("instructions"));
        recipe.setCookTime(bundle.getString("cookTime"));
        recipe.setNumOfReviewer(bundle.getInt("numOfReviewer"));
        return recipe;
    }




}
