package com.example.chenrui.easycook;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stephentuso.welcome.WelcomeHelper;

/***
 * LoginActivity
 *
 * Allows users to login to use the app
 ***/
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;
    private Button btnSignOut;
    private SignInButton btnGoogleSignIn;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;
    private TextView txt_creat;
    private TextView txt_forgot;
    WelcomeHelper welcomeScreen;

    // database setting
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        welcomeScreen = new WelcomeHelper(this, WelcomeScreenActivity.class);
        welcomeScreen.show(savedInstanceState);
        Utils.user = new User();

        setContentView(R.layout.activity_login);
        btnGoogleSignIn = (SignInButton)findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setOnClickListener(this);
        txt_creat = (TextView) findViewById(R.id.txt_create);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        // connect to the firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // get text from the user input
        mUsernameEditText = (EditText) findViewById(R.id.editTextLogin);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mSubmitButton = (Button) findViewById(R.id.submit);
        txt_creat.setOnClickListener(this);


        // once submit check the information in the database

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mUsernameEditText.getText().toString();
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                User profile = new User("",email,password);
                ProfileSaver profileSaver = new ProfileSaver();
                profileSaver.setProfile(profile);
                profileSaver.checkProfile(getBaseContext().getFilesDir(), new ProfileCallback() {
                    @Override
                    public void onCallback(User profile) {
                        if (profile.getEmail().equals(email) && profile.getPassword().equals(password)&&!password.equals("")) {
                            Log.i( " Your log", "You successfully login");
                            Intent myIntent = new Intent(LoginActivity.this, NavigateActivity.class);
                            Utils.username = profile.getUsername();
                            Utils.user = profile;
                            Toast.makeText(getBaseContext(),"Welcome " + Utils.username, Toast.LENGTH_SHORT).show();

                            startActivity(myIntent);
                        } else {
                            Toast.makeText(getBaseContext(),"Please login again", Toast.LENGTH_SHORT).show();
                        }
                    }
                },false);
            }
        });



    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }
    //TODO : fit for table

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGoogleSignIn:
                signIn();
                break;

            case R.id.txt_create:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;

        }


    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(LoginActivity.this, "Log Out Successfully ! ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /***
     * handleResult
     *
     * @param result    GoogleSignInResult  Stores Google login information
     *
     * Allows users to use their Google account to sign into the app
     */
    private void handleResult(GoogleSignInResult result){

        // Login successful
        if(result.isSuccess()){

            // Get account information
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri personPhotoUrl = account.getPhotoUrl();
            User profile = new User();
            profile.setUsername(name);
            profile.setEmail(email);
            profile.setPassword("");
            System.out.format("Login profile url: %s %s%n",profile.getUsername(),profile.getEmail());

            // Check to see if user already has a profile
            ProfileSaver profileSaver = new ProfileSaver();
            profileSaver.setProfile(profile);
            profileSaver.checkProfile(getBaseContext().getFilesDir(), new ProfileCallback() {
                @Override
                public void onCallback(User profile) {

                    // User doesn't have a profile yet. Has been created
                    if (profile.getEmail().equals("")) {
                        Utils.user = new User();
                        Utils.user.setEmail(email);
                        Utils.user.setUsername(name);
                        Utils.user.setPassword("");

                    // User already has a profile
                    } else {
                        Utils.user = profile;
                    }
                    Utils.username = Utils.user.getUsername();

//                    Utils.user.setUsername(account.getDisplayName());
                    Toast.makeText(LoginActivity.this, "Welcome " + Utils.username, Toast.LENGTH_SHORT).show();

                    System.out.format("Got profile: %s%n", Utils.user.getUsername());
                    Intent intent = new Intent(getBaseContext(), NavigateActivity.class);
                    startActivity(intent);
                }
            }, true);


        }
        else{
            Toast.makeText(LoginActivity.this, "Google Login failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==REQ_CODE && resultCode == Activity.RESULT_OK){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}

