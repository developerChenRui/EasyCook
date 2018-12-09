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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;


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


        addRecipe = view.findViewById(R.id.addRecipe);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(),CreateActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

}



class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.MyViewHolder> {
    private ArrayList<Recipe> recipeList;
    private Context context;


    public MyRecipesAdapter(ArrayList<Recipe> recipeList, Context context){
        this.context= context;
        this.recipeList = recipeList;

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
        /** if its image itself, should modify**/
//        myViewHolder.imgRecipe.setImageResource(this.recipeList.get(position).intValue());
        Picasso.get().load(this.recipeList.get(position).getRecipeImageURL()).into(myViewHolder.imgRecipe);

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
            this.context = context;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context,DishItemActivity.class);
            i.putExtras(Utils.Recipe2Bundle(recipeList.get(getAdapterPosition())));
            ((Activity)context).startActivityForResult(i,NavigateActivity.GETINGREDIENTS);
        }
    }
}