package com.example.chenrui.easycook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/***
 * PagerAdapter
 *
 * Controls navigation in the My Recipes fragment between Favorite, Public, and Private recipes
 ***/
public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0: // Favorites
                Favorites favorites = new Favorites();
                System.out.println("Starting favorites tab");
                return favorites;
            case 1: // My Public RecipesFragment
                MyRecipes myPublicRecipes = new MyRecipes();
                myPublicRecipes.setPubFlag(true);
//                System.out.println("Starting public tab");
                return myPublicRecipes;
            case 2: // My Private RecipesFragment
                MyRecipes myPrivateRecipes = new MyRecipes();
                return myPrivateRecipes;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
