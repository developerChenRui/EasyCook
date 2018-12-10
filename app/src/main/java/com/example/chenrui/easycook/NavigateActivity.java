package com.example.chenrui.easycook;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NavigateActivity extends AppCompatActivity implements UserProfile.UserProfileListener,TabRecipes.OnFragmentInteractionListener{


    // for the shopping list ingredients and for the rating returned from the DishItemActivity
    public static int GETINGREDIENTS = 1;
    ArrayList<String> shoppinglist= new ArrayList<>();
    //TODO fragments initialization
    // search recipe fragment
    private DiscoveryFragment dFrag;
    private FragmentManager fManager;
    private FragmentTransaction fTransaction;

    //shopping list fragment
    private ShoppingListFragment shoppingListFragment;

    // my favorite fragment
    public RecipesFragment favoriteFragment;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shoppinglist.clear();
        if(requestCode == GETINGREDIENTS && resultCode == Activity.RESULT_OK) {
            // get the ingredients from the dishitemActivity
            for(String s:data.getStringArrayListExtra("shoppingList")) {
                shoppinglist.add(s);
            }
            shoppingListFragment.initItems(shoppinglist);


            // for the returned Rating and returned # of reviewers
            float returnRating = data.getFloatExtra("returnRating",0);
            int  returnNumOfReviewers = data.getIntExtra("returnNumOfReviewers",0);
            CustomAdaptor cAdaptor = dFrag.returnAdaptor();
            cAdaptor.changeRecipeList(cAdaptor.justOpenPosition,returnRating,returnNumOfReviewers);
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void pickRecipeList(int i) {
        favoriteFragment.pickRecipeList(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        // hide the title of actionbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // search recipe fragment
        fManager = getSupportFragmentManager();
        fTransaction = fManager.beginTransaction();

        // Added by Justin for My RecipesFragment tab

        dFrag = new DiscoveryFragment();
        fTransaction.add(R.id.FragLayout,dFrag,"Discovery");


        favoriteFragment = new RecipesFragment();
        fTransaction.add(R.id.FragLayout,favoriteFragment,"favoriteRecipes");



        // shopping list fragment
        shoppingListFragment = new ShoppingListFragment();
        fTransaction.add(R.id.FragLayout,shoppingListFragment,"ShoppingList");



    //    fTransaction.add(R.id.FragLayout,dFrag,"Discovery");


        fTransaction = fManager.beginTransaction();
        fTransaction.replace(R.id.FragLayout,dFrag);


        fTransaction.commit();


        // bottom navigation initialization
        // -search -shopping list -my recipe
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        // Set Item click listener to the menu items
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override

                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        switch (item.getItemId()) {
                            case R.id.search:
                                fTransaction = fManager.beginTransaction();
                                fTransaction.replace(R.id.FragLayout,dFrag);
                                fTransaction.commit();
                                break;
                            case R.id.shoppingList:
                                fTransaction = fManager.beginTransaction();
                                fTransaction.replace(R.id.FragLayout,shoppingListFragment);
                                fTransaction.commit();
                                break;
                            case R.id.favorite:
                                fTransaction = fManager.beginTransaction();
                                fTransaction.replace(R.id.FragLayout,favoriteFragment);
                                fTransaction.commit();
                                break;

                        }
                        return false;
                    }
                });

        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(NavigateActivity.this);
                } else {
                    Toast.makeText(NavigateActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void onResume() {
        Intent i = getIntent();
        int id = i.getIntExtra("id", 0);

        if(id == 1) {
            FragmentManager fmanger = getSupportFragmentManager();
            FragmentTransaction transaction = fmanger.beginTransaction();

            Intent intent=new Intent();
            intent.setClass(NavigateActivity.this, RecipesFragment.class);
            intent.putExtra("id",1);
            transaction.replace(R.id.FragLayout, favoriteFragment);
            transaction.commit();
        }

        super.onResume();
    }
}
