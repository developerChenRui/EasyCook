package com.example.chenrui.easycook;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class ValidateUserInfo extends AppCompatActivity {

    public static boolean isNameValid(Context context,String name) {
        if (name.equals("")) {
            Toast.makeText(context,"Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean isEmailValid(Context context,String email) {

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        if (!m.matches()) {
            Toast.makeText(context,"Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return m.matches();
    }

    public static boolean isPasswordValid(Context context,String password, String confirmPassword) {

        if(password.length()<6 && confirmPassword.length()<6){
            Toast.makeText(context,"Password under 6 characters, Please enter more", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!password.equals(confirmPassword)){
            Toast.makeText(context,"Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
