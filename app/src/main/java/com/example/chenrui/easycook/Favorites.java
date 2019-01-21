package com.example.chenrui.easycook;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 * Displays the user's favorited recipes
 */
public class Favorites extends Fragment {
    private RecyclerView lstFavorites;
    private FavoritesAdapter rvAdapter;
    private ArrayList<Recipe> recipeList;
    private RecipeSaver recipeSaver;

    public Favorites() {
        // Required empty public constructor
    }

    /***
     * onCreateView
     *
     * @param inflater               LayoutInflater  Inflater to inflate the fragment
     * @param container              ViewGroup       Viewgroup to inflate the fragment
     * @param savedInstanceState     Bundle          SavedInstanceState bundle
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ProfileSaver profileSaver = new ProfileSaver();
        profileSaver.updateProfile(Utils.user,getContext().getFilesDir());

        lstFavorites = view.findViewById(R.id.lstFavorites);
        recipeSaver = new RecipeSaver();
        recipeList = new ArrayList<>();
        /** hook up database **/
        Toast.makeText(getContext(), "loading data, please wait", Toast.LENGTH_LONG).show();
        JSONArray idArr = Utils.user.getFavoriteRecipes();
        if (idArr == null) idArr = new JSONArray();
        try{

            // Go through every favorited recipe
            for (int i = 0; i < idArr.length(); i++){
                String id = idArr.getString(i);
                /** dangerous concurrency !!!!!!!!!! **/

                // Spoonacular recipe
                if (isDigit(id)){
                    Utils.recipeIdSearch(id, new AsyncData() {
                        @Override
                        public void onData(ArrayList<Recipe> recipeList) {
                            Favorites.this.recipeList.add(recipeList.get(0));
                            if (rvAdapter == null){
                                rvAdapter = new FavoritesAdapter(Favorites.this.recipeList, getContext());
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                lstFavorites.setLayoutManager(mLayoutManager);
                                lstFavorites.setItemAnimator(new DefaultItemAnimator());
                                lstFavorites.setAdapter(rvAdapter);
                            }else {
                                rvAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(getContext(),"There is an error during loading data", Toast.LENGTH_SHORT).show();
                            Log.d("YANG", "onError: " + errorMessage);
                        }
                    });

                // User recipe
                }else {
                    Favorites.this.recipeSaver.fetchRecipe(id, new RecipeCallback() {
                        @Override
                        public void onCallBack(JSONArray value) {
                            try {
                                Favorites.this.recipeList.add((Recipe) value.get(0));
                                if (rvAdapter == null){
                                    rvAdapter = new FavoritesAdapter(Favorites.this.recipeList, getContext());
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                    lstFavorites.setLayoutManager(mLayoutManager);
                                    lstFavorites.setItemAnimator(new DefaultItemAnimator());
                                    lstFavorites.setAdapter(rvAdapter);
                                }else {
                                    rvAdapter.notifyDataSetChanged();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        rvAdapter = new FavoritesAdapter(recipeList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lstFavorites.setLayoutManager(mLayoutManager);
        lstFavorites.setItemAnimator(new DefaultItemAnimator());
        lstFavorites.setAdapter(rvAdapter);
        return view;
    }

    private boolean isDigit(String id){
        try{
            Double.parseDouble(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}



class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> {
    private ArrayList<Recipe> recipeList;
    private Context context;

    public FavoritesAdapter(ArrayList<Recipe> recipeList, Context context){
        this.recipeList = recipeList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_row,viewGroup,false);
        return new MyViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.txtRecipe.setText(this.recipeList.get(position).getRecipeName());
        try{
            Picasso.get().load(this.recipeList.get(position).getRecipeImageURL()).into(myViewHolder.imgRecipe);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.recipeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgRecipe;
        private TextView txtRecipe;
        private Context context;

        public MyViewHolder(View itemView, Context context){
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            txtRecipe = itemView.findViewById(R.id.txtRecipe);
            itemView.setOnClickListener(this);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context,DishItemActivity.class);
            i.putExtra("id",1);
            i.putExtras(Utils.Recipe2Bundle(recipeList.get(getAdapterPosition())));
            ((Activity)context).startActivityForResult(i,NavigateActivity.GETINGREDIENTS);
        }
    }
}