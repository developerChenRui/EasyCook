package com.example.chenrui.easycook;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Recipes extends Fragment implements UserProfile.UserProfileListener {
    // TODO: Rename parameter arguments, choose names that match
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



    public Recipes() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
//        menuMyRecipes = (NavigationView) view.findViewById(R.id.menuMyRecipes);

//        menuMyRecipes.setNavigationItemSelectedListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        fm = getChildFragmentManager();
//        myRecipes = new MyRecipes();
//        favorites = new Favorites();
//        FragmentTransaction ft = fm.beginTransaction();
////        ft.add(R.id.recipesLayout,favorites,"tag1");
//        ft.addToBackStack("add favorites");
//        ft.commit();

    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        menuItem.setChecked(true);
//        FragmentTransaction ft = fm.beginTransaction();
//        switch (menuItem.getItemId()){
//            case R.id.myrecipes:
//                ft.detach(favorites);
//                ft.attach(myRecipes);
//                ft.commit();
//                break;
//            case R.id.favorites:
//                ft.detach(myRecipes);
//                ft.attach(favorites);
//                ft.commit();
//                break;
//        }
//        return false;
//    }

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
}
