package com.example.chenrui.easycook;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecipesFragment extends Fragment implements UserProfile.UserProfileListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private ImageView imgUser;
    private TextView txtUser;
//    private NavigationView menuMyRecipes;
    private MyRecipes myRecipes;
    private Favorites favorites;
//    private UserProfile userProfile;
    private FragmentManager fm;



    public RecipesFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


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

        setHasOptionsMenu(true);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                /** implement later**/
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
