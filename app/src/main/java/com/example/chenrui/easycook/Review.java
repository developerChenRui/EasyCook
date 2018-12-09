package com.example.chenrui.easycook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;


public class Review {
    private String username;
    private String profileImgURL;
    private String text;
    private float rating;
    private JSONArray userLikes = new JSONArray();
    private Timestamp timestamp;

    Review(String username, String profileImgURL, String text, float rating, Timestamp timestamp) {
        this.username = username;
        this.profileImgURL = profileImgURL;
        this.text = text;
        this.rating = rating;
        this.timestamp = timestamp;

    }

    Review() {

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfileImgURL(String imgURL){
        this.profileImgURL = imgURL;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void addUserLike(String username) {
        this.userLikes.put(username);
    }

    public void setTimestamp() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getUsername(){
        return this.username;
    }

    public String getProfileImgURL() {
        return this.profileImgURL;
    }

    public String getText() {
        return this.text;
    }

    public float getRating() {
        return this.rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getRelativeTime() {
        long relative = System.currentTimeMillis() - this.timestamp.getTime();
        if (relative >= (long)1000*60*60*24*365) {
            return (int) (relative / 1000 * 60 * 60 * 24 * 30) + " years";
        } else if (relative >= (long)1000*60*60*24*30) {
            return (int) (relative / 1000 * 60 * 60 * 24 * 30) + " months";
        } else if (relative >= (long)1000*60*60*24*7) {
            return (int)(relative/1000*60*60*24*7) + " weeks";
        } else if (relative >= (long)1000*60*60*24) {
            return (int)(relative/1000*60*60*24) + " days";
        } else if (relative >= (long)1000*60*60) {
            return (int)(relative/1000*60*60) + " hours";
        } else if (relative >= (long)1000*60) {
            return (int)(relative/1000*60) + " minutes";
        } else {
            return "Just now";
        }
    }

    // Converts review to JSON
    public JSONObject toJSON() {
        JSONObject out = new JSONObject();
        try {
            out.put("username",this.username);
            out.put("profileImgURL",this.profileImgURL);
            out.put("text",this.text);
            out.put("userLikes",this.userLikes);
            out.put("rating",this.rating);
            out.put("timestamp",this.timestamp);
        } catch (JSONException e) {

        }
        return out;
    }

    public void fromJSON(JSONObject reviewJSON) {
        try {
            this.username = reviewJSON.getString("username");
            this.profileImgURL = reviewJSON.getString("profileImgURL");
            this.text = reviewJSON.getString("text");
            this.userLikes = reviewJSON.getJSONArray("userLikes");
            this.rating = (float)reviewJSON.getDouble("rating");
            this.timestamp = (Timestamp)reviewJSON.get("timestamp");
        } catch (JSONException e) {

        }
    }
}
