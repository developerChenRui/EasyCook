package com.example.chenrui.easycook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/***
 * Review
 *
 * Stores all relevant information for a review
 ***/
public class Review {
    private String username;
    private String email;
    private String profileImgURL;
    private String text;
    private float rating;
    private JSONArray userLikes = new JSONArray();
    private Timestamp timestamp;



    Review(String username, String email, String profileImgURL, String text, float rating, Timestamp timestamp) {
        this.username = username;
        this.email = email;
        this.profileImgURL = profileImgURL;
        this.text = text;
        this.rating = rating;
        this.timestamp = timestamp;
//        userLikes.put
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

    public JSONArray getUserLikes() {
        return userLikes;
    }

    public void addUserLike(String email) {
        try {
            for (int i = 0; i < this.userLikes.length(); i++) {
                if (this.userLikes.getString(i).equals(email)) {
                    return;
                }
            }
            this.userLikes.put(email);
        } catch (JSONException e) {

        }
    }

    public void removeUserLike(String email) {
        try {
            for (int i = 0; i < this.userLikes.length(); i++) {
                if (this.userLikes.getString(i).equals(email)) {
                    this.userLikes.remove(i);
                    return;
                }
            }
        } catch (JSONException e) {

        }
    }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() {return this.email; }

    public String getRelativeTime() {
        long relative = System.currentTimeMillis() - timestamp.getTime();
        String unit;
        int time;
        if (relative >= (1000*60*60*24*365L)) {
            time = (int) (relative / (1000 * 60 * 60 * 24 * 30L));
            unit = " year";
        } else if (relative >= (1000*60*60*24*30L)) {
            time = (int) (relative / (1000 * 60 * 60 * 24 * 30L));
            unit = " month";
        } else if (relative >= (1000*60*60*24*7L)) {
            time = (int)(relative/(1000*60*60*24*7L));
            unit = " week";
        } else if (relative >= (1000*60*60*24L)) {
            time = (int)(relative/(1000*60*60*24L));
            unit = " day";
        } else if (relative >= (1000*60*60L)) {
            time = (int)(relative/(1000*60*60L));
            unit = " hour";
        } else if (relative >= (1000*60L)) {
            time = (int)(relative/(1000*60L));
            unit = " minute";
        } else {
            return "Just now";
        }
        if (time == 1) {
            return time + unit;
        } else {
            return time + unit + "s";
        }
    }

    // Converts review to JSON
    public JSONObject toJSON() {
        JSONObject out = new JSONObject();
        try {
            out.put("username",this.username);
            out.put("email",this.email);
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
            this.email = reviewJSON.getString("email");
            this.profileImgURL = reviewJSON.getString("profileImgURL");
            this.text = reviewJSON.getString("text");
            this.userLikes = reviewJSON.getJSONArray("userLikes");
            this.rating = (float)reviewJSON.getDouble("rating");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            try {
                Date parsedDate = dateFormat.parse(reviewJSON.getString("timestamp"));
                this.timestamp = new Timestamp((parsedDate.getTime()));
                System.out.format("Got timestamp %s%n",this.timestamp);
            } catch (Exception e) {
                System.err.format("Timestamp not filled in properly for %s: %s%n",this.username,e);

            }

        } catch (JSONException e) {

        }
    }
}
