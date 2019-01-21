package com.example.chenrui.easycook;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment implements UserProfile.UserProfileListener {

    private ImageView imgUser;
    private TextView txtUser;
    private ImageView addRecipe;
//    private NavigationView menuMyRecipes;
    private MyRecipes myRecipes;
    private Favorites favorites;
//    private UserProfile userProfile;
    private FragmentManager fm;
    String username;
    GoogleApiClient mGoogleApiClient;

    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private int selectMax = 1;
    private static int themeId = R.style.picture_white_style;
    private List<LocalMedia> picList = new ArrayList<>();

    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    private int chooseMode = PictureMimeType.ofAll();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        EditText txtUser = (EditText) view.findViewById(R.id.textView);
        if (!Utils.username.equals("")) {
            txtUser.setText(Utils.username);
        }


        // Detect when a user changes their username and push to database
        username = txtUser.getText().toString();
        txtUser.addTextChangedListener(new TextWatcher()  {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (txtUser.getText().toString().trim().length() == 0) {
                    txtUser.setError("Enter Username");
                } else {
                    txtUser.setError(null);
                }
                username = txtUser.getText().toString();
                Utils.username = txtUser.getText().toString();
                Utils.user.setUsername(Utils.username);

                // Need to update user made recipes accordingly
                JSONArray recipes = Utils.user.getPublicRecipes();
                RecipeSaver recipeSaver = new RecipeSaver();
                for (int i = 0; i < recipes.length(); i++) {
                    try{
                        recipeSaver.updateUser(Utils.user.getProfileImgURL(),Utils.username,recipes.getString(i));
                    } catch (JSONException e) {

                    }
                }
            }
        });

        // Update username when user has moved away from edittext
        txtUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && txtUser.getText().toString().trim().length() == 0) {
                    txtUser.setText(username);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else{
                    username = txtUser.getText().toString();
                    Utils.username = txtUser.getText().toString();
                    Utils.user.setUsername(Utils.username);

                    // Update usermade recipes accordingly
                    JSONArray recipes = Utils.user.getPublicRecipes();
                    RecipeSaver recipeSaver = new RecipeSaver();
                    for (int i = 0; i < recipes.length(); i++) {
                        try{
                            recipeSaver.updateUser(Utils.user.getProfileImgURL(),Utils.username,recipes.getString(i));
                        } catch (JSONException e) {

                        }
                    }
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });


        // Set user image
        imgUser = (ImageView)view.findViewById(R.id.imgUserPic);

        if (!Utils.user.getProfileImgURL().equals("")) {
            Picasso.get().load(Utils.user.getProfileImgURL()).transform(new PicassoCircleTransformation()).into(imgUser);
        }

        // Let user update profile picture
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnAddPicClickListener = new onAddPicClickListener() {
                    @Override
                    public void onAddPicClick() {
                        gallery(picList, PictureConfig.CHOOSE_REQUEST);
                    }
                };
                PictureSelector.create(RecipesFragment.this).openGallery(PictureMimeType.ofImage())
                        .forResult(PictureConfig.CHOOSE_REQUEST);

                Log.i("select", ""+picList.size());

            }
        });

        // New recipe creation button
        addRecipe = (ImageView)view.findViewById(R.id.addRecipe);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),CreateActivity.class);
                startActivity(i);

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void pickRecipeList(int i) {
        if (myRecipes == null) {
            myRecipes = new MyRecipes();
        }
        if (favorites == null) {
            favorites = new Favorites();
        }
        fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (i) {
            case R.id.favorites:
                ft.detach(myRecipes);
                ft.attach(favorites);
                ft.commit();
                break;
            case R.id.myrecipes:
                ft.detach(favorites);
                ft.attach(myRecipes);
                ft.commit();
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.my_recipes_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                Intent i=new Intent(getContext(),LoginActivity.class);
                                startActivity(i);
                            }
                        });
                Toast.makeText(getContext(),"Successfully logged Out",Toast.LENGTH_SHORT).show();
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                // User has changed their profile picture
                case PictureConfig.CHOOSE_REQUEST:
                    picList = PictureSelector.obtainMultipleResult(data);
                    setList(picList);
                    if(!picList.isEmpty()) {
                        LocalMedia media = picList.get(0);
                        String path = "";
                        if (media.isCut() && !media.isCompressed()) {
                            path = media.getCutPath();
                        } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                            path = media.getCompressPath();
                        } else {
                            path = media.getPath();
                        }
                        if (media.isCompressed()) {
                            Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                            Log.i("compress path:", media.getCompressPath());
                        }

                        Log.i("origin path:", media.getPath());
                        int pictureType = PictureMimeType.isPictureType(media.getPictureType());
                        if (media.isCut()) {
                            Log.i("crop path:", media.getCutPath());
                        }

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.portrait_photo)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(RecipesFragment.this)
                                .load(path)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(imgUser);
                        setHasOptionsMenu(true);
                        System.out.println("Updated imgUser");



                        Bitmap bm = getBitmap(path);

                        // Push new image to cloud storage
                        ImageSaver imageSaver = new ImageSaver();
                        imageSaver.pushImage(Utils.user.getCleanEmail(),bm,getContext().getFilesDir(), new ImageCallback() {
                            @Override
                            public void onCallback(String imageURL) {
                                System.out.format("Uploaded user image URL: %s%n",imageURL);
                                Utils.user.setProfileImgURL(imageURL);
                                ProfileSaver profileSaver = new ProfileSaver();
                                profileSaver.updateProfile(Utils.user,getContext().getFilesDir());

                                JSONArray publicRecipes = Utils.user.getPublicRecipes();

                                // Update all public recipes of profile change
                                for (int i = 0; i < publicRecipes.length(); i++) {
                                    try {
                                        RecipeSaver recipeSaver = new RecipeSaver();
                                        recipeSaver.updateUser(imageURL,Utils.username,publicRecipes.getString(i));
                                    } catch (JSONException e) {

                                    }
                                }
                            }
                        });
                    }
                    break;
            }
        }
    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        File file = new File(path);
        Uri uri = Utils.getImageContentUri(getContext(), file);
        System.out.print("uri" + uri.toString());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void setList(List<LocalMedia> picList) {
        this.picList = picList;
    }


    private void gallery(List<LocalMedia> list, int requestCode) {
            PictureSelector.create(RecipesFragment.this)
                    .openGallery(chooseMode)
                    .theme(R.style.picture_white_style)
                    .maxSelectNum(1)
                    .minSelectNum(1)
                    .imageSpanCount(4)
                    .selectionMode(PictureConfig.SINGLE)
                    .previewImage(true)
                    .isCamera(true)
                    .isZoomAnim(true)
                    .enableCrop(true)
                    .compress(true)
                    .synOrAsy(true)
                    .circleDimmedLayer(true)
                    .glideOverride(100, 100)
                    .withAspectRatio(1, 1)
                    .hideBottomControls(true)
                    .isGif(false)
                    .freeStyleCropEnabled(true)
                    .circleDimmedLayer(false)
                    .showCropFrame(false)
                    .showCropGrid(false)
                    .openClickSound(false)
                    .selectionMedia(list)
                    .minimumCompressSize(100)
                    .forResult(requestCode);

    }
}
