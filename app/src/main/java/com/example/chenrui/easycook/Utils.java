package com.example.chenrui.easycook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


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
        bundle.putString("recipeImage",recipe.getRecipeImageURL());
        bundle.putString("profile",recipe.getProfileURL());
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
        recipe.setRecipeImageURL(bundle.getString("recipeImage"));
        recipe.setProfileURL(bundle.getString("profile"));
        recipe.setMakerName(bundle.getString("makerName",recipe.getMakerName()));
        recipe.setIngredients(bundle.getStringArrayList("ingredients"));
        recipe.setInstructions(bundle.getStringArrayList("instructions"));
        recipe.setCookTime(bundle.getString("cookTime"));
        recipe.setNumOfReviewer(bundle.getInt("numOfReviewer"));
        return recipe;
    }


    // http request


    private static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/";
    private static final String API_KEY = "ua9TN5jI9Zmsh2GQruoJx9GDuB6kp16z22FjsnpoTwy1GJRizA";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("X-RapidAPI-Key",API_KEY);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("X-RapidAPI-Key",API_KEY);
        client.addHeader("Content-Type", "application/json");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String TAG = "YANG";
    private static ArrayList<Recipe> recipeList = new ArrayList<>();

    public static ArrayList<Recipe> randomSearch(){
  //      while(recipeList.size()==0) {

            AsyncHttpRequest.get("recipes/random?number=10", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        recipeList = new ArrayList<>();

                        Log.d("checkNUm5", "Successfully");
                        Recipe newRecipe;
                        JSONObject jsonObj = new JSONObject(response.toString());
                        JSONArray recipes = jsonObj.getJSONArray("recipes");
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject recipe = recipes.getJSONObject(i);
                            String title = recipe.getString("title");
                            Log.d(TAG, "onSuccess: title " + title);
                            String id = recipe.getString("id");
                            Log.d(TAG, "onSuccess: id " + id);
                            String imageURL = recipe.getString("image");
                            Log.d(TAG, "onSuccess: image " + imageURL);
                            ArrayList<String> ingredients = new ArrayList<>();
                            JSONArray ingredArr = recipe.getJSONArray("extendedIngredients");
                            for (int j = 0; j < ingredArr.length(); j++) {
                                JSONObject ingredObj = (JSONObject) ingredArr.get(j);
                                ingredients.add(ingredObj.getString("name"));
                            }
                            Log.d(TAG, "onSuccess: ingredients " + ingredients.toString());
                            newRecipe = new Recipe(title, ingredients, imageURL, 0, id);
                            recipeList.add(newRecipe);
                            Log.d("checkNUm6", recipeList.size() + "");
                        }
                        Log.d("checkNUm8", recipeList.size() + "");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "onFailure: " + errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d(TAG, "onFailure: " + throwable.toString());
                }
            });


     //   }
        Log.d("checkNUm7", recipeList.size() + "");
        return recipeList;

    }


    private static ArrayList<Recipe> recipeListKeyWord = new ArrayList<>();

    public static ArrayList<Recipe> keyWordSearch(String keyWord){
 //       ArrayList<Recipe> recipeList = new ArrayList<>();
        if (keyWord != null && keyWord.length() != 0){
            keyWord.replace(" ","+");
            AsyncHttpRequest.get("recipes/search?number=20&offset=0&query=" + keyWord, null, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        Recipe newRecipe;
                        JSONObject jsonObj = new JSONObject(response.toString());
                        JSONArray recipes = jsonObj.getJSONArray("results");
                        for (int i = 0 ; i < recipes.length(); i++){
                            JSONObject recipe = recipes.getJSONObject(i);
                            String title = recipe.getString("title");
                            Log.d(TAG, "onSuccess: title " + title);
                            String id = recipe.getString("id");
                            Log.d(TAG, "onSuccess: id " + id);
                            String imageURL = recipe.getString("image");
                            Log.d(TAG, "onSuccess: image " + imageURL);
                            String[] urlArr = imageURL.split("-");
                            String[] imageIdArr = urlArr[urlArr.length - 1].split("\\.");
                            String imageId = imageIdArr[0];
                            String imageFormat = imageIdArr[1];
                            String realImageURL = "https://spoonacular.com/recipeImages/" + imageId + "-556x370." + imageFormat;
                            ArrayList<String> ingredients = new ArrayList<>();
                            newRecipe = new Recipe(title,ingredients,realImageURL,0,id);
                            recipeListKeyWord.add(newRecipe);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "onFailure: " + errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d(TAG, "onFailure: " + throwable.toString());
                }
            });


        }

        return recipeListKeyWord;
    }


    static Recipe specificRecipe;

    public static Recipe recipeIdSearch(String recipeID){
        Log.d(TAG, "recipeIdSearch: begin");
 //       ArrayList<Recipe> recipeList = new ArrayList<>();
        if (recipeID != null){
            Log.d(TAG, "recipeIdSearch: if ");
            AsyncHttpRequest.get("recipes/" + recipeID + "/information",null, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try{
                        Log.d(TAG, "recipeIdSearch: ");
                        JSONObject jsonObj = new JSONObject(response.toString());
                        Log.d(TAG, "recipeIdSearchon: 1");
                        String title = jsonObj.getString("title");
                        Log.d(TAG, "recipeIdSearch: title " + title);
                        String cookMinutes = jsonObj.getString("readyInMinutes"); /**!!!!cooktime**/
                        Log.d(TAG, "recipeIdSearch: cookMinutes " + cookMinutes);
                        String ImageURL = jsonObj.getString("image");
                        Log.d(TAG, "recipeIdSearch: ImageURL " + ImageURL);
                        ArrayList<String> ingredients = new ArrayList<>();
                        JSONArray ingredArr = jsonObj.getJSONArray("extendedIngredients");
                        for (int j = 0; j < ingredArr.length(); j++){
                            JSONObject ingredObj = (JSONObject)ingredArr.get(j);
                            ingredients.add(ingredObj.getString("name"));
                        }
                        List<String> stepList = new ArrayList<>(); /**!!!!!!steps**/
                        JSONArray stepsArr = jsonObj.getJSONArray("analyzedInstructions");
                        JSONObject stepsArrObj = (JSONObject)stepsArr.get(0);
                        JSONArray realStepsArr = stepsArrObj.getJSONArray("steps");
                        for (int i = 0; i < realStepsArr.length(); i++){
                            JSONObject step = (JSONObject)realStepsArr.get(i);
                            stepList.add(step.getString("step"));
                        }
                        specificRecipe= new Recipe();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "onFailure: " + errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d(TAG, "onFailure: " + throwable.toString());
                }
            });

        }

        return specificRecipe;


    }




}
