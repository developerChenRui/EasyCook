package com.example.chenrui.easycook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chenrui on 2018/8/8.
 */



public class User {
    private String username = "";
    private String email = "";
    private String password = "";

    private String profileImgURL = "";
    private JSONArray shoppingList = new JSONArray();
    private JSONArray publicRecipes = new JSONArray();
    private JSONArray favoriteRecipes = new JSONArray();
    private JSONArray privateRecipes = new JSONArray();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONArray getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(JSONArray favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public JSONArray getPrivateRecipes() {
        return privateRecipes;
    }

    public JSONArray getPublicRecipes() {
        return publicRecipes;
    }

    public String getProfileImgURL() {
        return profileImgURL;
    }

    public JSONArray getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(JSONArray shoppingList) {
        this.shoppingList = shoppingList;
    }


    public String getCleanEmail() {
        return email.replace('@','_').replace('.','_');
    }

    public User() {}


    public User(final String username, final String email) {
        this.username = username;
        this.email = email;
    }

    public User(final String username, final String email, final String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

//    public User(final String username, final String email, final String password) {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//        this.username = username;
//        this.email = email;
//        this.password = password;
//    }

    public User(String username, String email, String password, String profileImgURL, JSONArray shoppingList, JSONArray publicRecipes, JSONArray favoriteRecipes) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImgURL = profileImgURL;
        this.shoppingList = shoppingList;
        this.publicRecipes = publicRecipes;
        this.favoriteRecipes = favoriteRecipes;
    }

    public JSONObject toJSON() {
        JSONObject out = new JSONObject();
        try {
            out.put("username",this.username);
            out.put("email",this.email);
            out.put("password",this.password);
            out.put("profileImgURL",this.profileImgURL);
            out.put("shoppingList",this.shoppingList);
            out.put("publicRecipes",this.publicRecipes);
            out.put("favoriteRecipes",this.favoriteRecipes);
            out.put("privateRecipes",this.privateRecipes);
        } catch (JSONException e) {

        }
        return out;
    }

    public void fromJSON(JSONObject profile) {
        try {
            this.username = profile.getString("username");
            this.email = profile.getString("email");
            this.password = profile.getString("password");
            this.profileImgURL = profile.getString("profileImgURL");
            this.shoppingList = profile.getJSONArray("shoppingList");
            this.publicRecipes = profile.getJSONArray("publicRecipes");
            this.favoriteRecipes = profile.getJSONArray("favoriteRecipes");
            this.privateRecipes = profile.getJSONArray("privateRecipes");
        } catch (JSONException e) {

        }
    }
}


