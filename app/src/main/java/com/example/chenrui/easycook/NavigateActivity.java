package com.example.chenrui.easycook;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class NavigateActivity extends AppCompatActivity implements UserProfile.UserProfileListener,TabRecipes.OnFragmentInteractionListener{

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
        dFrag = new DiscoveryFragment();
        fTransaction.add(R.id.FragLayout,dFrag,"Discovery");


        // shopping list fragment
        shoppingListFragment = new ShoppingListFragment();
        fTransaction.add(R.id.FragLayout,shoppingListFragment,"ShoppingList");



        // Added by Justin for My RecipesFragment tab
        favoriteFragment = new RecipesFragment();
        fTransaction.add(R.id.FragLayout,favoriteFragment,"favoriteRecipes");

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
                                //TODO
                                if(dFrag == null) {
                                    dFrag = new DiscoveryFragment();
                                }
                                fTransaction = fManager.beginTransaction();
                                fTransaction.replace(R.id.FragLayout,dFrag);
                                fTransaction.commit();
                              //  Toast.makeText(NavigateActivity.this,"Search Fragment",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.shoppingList:
                                //TODO
                                if(shoppingListFragment == null) {
                                    shoppingListFragment = new ShoppingListFragment();
                                }
                                fTransaction = fManager.beginTransaction();
                                fTransaction.replace(R.id.FragLayout,shoppingListFragment);
                                fTransaction.commit();
                               // Toast.makeText(NavigateActivity.this,"shoppinglist Fragment",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.favorite:
                                //TODO
                                if(favoriteFragment == null) {
                                    favoriteFragment = new RecipesFragment();
                                }
                                fTransaction = fManager.beginTransaction();
                                fTransaction.replace(R.id.FragLayout,favoriteFragment);
                                fTransaction.commit();
                                Toast.makeText(NavigateActivity.this,"favorite Fragment",Toast.LENGTH_SHORT).show();
                                break;

                        }
                        return false;
                    }
                });
    }
}
