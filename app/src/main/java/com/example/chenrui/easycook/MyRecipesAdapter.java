package com.example.chenrui.easycook;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.MyViewHolder> {
    private RecyclerViewClickListener clickListener;
    private String[] recipeNames;
    private ArrayList<Integer> recipeImages;


    public MyRecipesAdapter(String[] recipeNames, ArrayList<Integer> recipeImages){
        this.recipeNames= recipeNames;
        this.recipeImages = recipeImages;

    }

    public void setClickListener(RecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_row,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        myViewHolder.txtRecipe.setText(this.recipeNames[position]);
        myViewHolder.imgRecipe.setImageResource(recipeImages.get(position).intValue());

    }

    @Override
    public int getItemCount() {
        return this.recipeNames.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgRecipe;
        public TextView txtRecipe;

        public MyViewHolder(View itemView){
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            txtRecipe = itemView.findViewById(R.id.txtRecipe);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());

        }
    }
}


