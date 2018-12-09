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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Favorites extends Fragment {
    private RecyclerView lstFavorites;
    private FavoritesAdapter rvAdapter;

    public Favorites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        lstFavorites = view.findViewById(R.id.lstFavorites);
        ArrayList<Recipe> recipeList = new ArrayList<>();
        /** hook up database **/
        rvAdapter = new FavoritesAdapter(recipeList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lstFavorites.setLayoutManager(mLayoutManager);
        lstFavorites.setItemAnimator(new DefaultItemAnimator());
        lstFavorites.setAdapter(rvAdapter);
        return view;
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
            Utils.recipeIdSearch(recipeList.get(getAdapterPosition()).getRecipeId(), new AsyncData() {
                @Override
                public void onData(ArrayList<Recipe> recipeList) {
                    Recipe recipe = recipeList.get(0);
                    Log.d("CHECKK1",recipe.getRecipeId());
                    i.putExtras(Utils.Recipe2Bundle(recipe));
                    ((Activity)context).startActivityForResult(i,NavigateActivity.GETINGREDIENTS);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(context,"There is an error retrieving data", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}