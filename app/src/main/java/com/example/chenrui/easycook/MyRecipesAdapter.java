package com.example.chenrui.easycook;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;


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
        System.out.format("Recipe Image URL %s: %s%n",this.recipeList,this.recipeList.get(position).getRecipeImageURL());
        if (!this.recipeList.get(position).getRecipeImageURL().equals("")) {
            Picasso.get().load(this.recipeList.get(position).getRecipeImageURL()).into(myViewHolder.imgRecipe);
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
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context,DishItemActivity.class);
            System.out.format("Picked recipe: %s%n", recipeList.get(getAdapterPosition()).getRecipeId());
            i.putExtras(Utils.Recipe2Bundle(recipeList.get(getAdapterPosition())));
            ((Activity)context).startActivityForResult(i,NavigateActivity.GETINGREDIENTS);
        }
    }
}


