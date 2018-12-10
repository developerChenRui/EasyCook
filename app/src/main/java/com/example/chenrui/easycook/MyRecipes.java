package com.example.chenrui.easycook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import org.json.JSONArray;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecipes extends Fragment{
    private RecyclerView lstRecipes;
    private MyRecipesAdapter rvAdapter;
    private Button addRecipe;
    private ArrayList<Recipe> recipeList;
    private boolean pubFlag = false;
    private RecipeSaver recipeSaver;
    JSONArray idArr;

    public MyRecipes() {
        // Required empty public constructor
    }



    public void setPubFlag(boolean pubFlag) {
        this.pubFlag = pubFlag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        lstRecipes = view.findViewById(R.id.lstRecipes);
        recipeList= new ArrayList<>();
        recipeSaver = new RecipeSaver();
        /** do something to pass in the recipeList**/
        Toast.makeText(getContext(),"loading data, please wait", Toast.LENGTH_LONG).show();
        if (pubFlag) idArr = Utils.user.getPublicRecipes();
        else idArr = Utils.user.getPrivateRecipes();
        if (idArr == null) idArr = new JSONArray();
        recipeSaver.fetchRecipes(idArr, new RecipeCallback() {
            @Override
            public void onCallBack(JSONArray value) {
                try{
                    for (int i = 0; i < value.length(); i++){
                        recipeList.add((Recipe) value.get(i));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                rvAdapter = new MyRecipesAdapter(recipeList, getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                lstRecipes.setLayoutManager(mLayoutManager);
                lstRecipes.setItemAnimator(new DefaultItemAnimator());
                lstRecipes.setAdapter(rvAdapter);
            }
        });


        return view;
    }

}


