package com.example.chenrui.easycook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chenrui.easycook.CreateRecipes.CreateActivity;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecipes extends Fragment implements RecyclerViewClickListener {
    private RecyclerView lstRecipes;
    private MyRecipesAdapter rvAdapter;
    private Button addRecipe;

    public MyRecipes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        lstRecipes = view.findViewById(R.id.lstRecipes);
        ArrayList<Integer> recipeImages = new ArrayList<Integer>();
        recipeImages.add(R.drawable.menulist);
        String[] recipeNames = new String[1];
        recipeNames[0] = "Test";
        rvAdapter = new MyRecipesAdapter(recipeNames, recipeImages);
        rvAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lstRecipes.setLayoutManager(mLayoutManager);
        lstRecipes.setItemAnimator(new DefaultItemAnimator());
        lstRecipes.setAdapter(rvAdapter);

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

    @Override
    public void onClick(View view, int position) {

    }
}

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


