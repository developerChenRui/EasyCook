package com.example.chenrui.easycook;

/**
 * Created by chenrui on 2018/8/8.
 */



public class User {
    private String username;
    private String email;
    private String password;
    private long time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public User(final String username, final String email, final String password, final long timeStamp) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.username = username;
        this.email = email;
        this.password = password;
        this.time = timeStamp;
    }
}


