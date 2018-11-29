package com.example.chenrui.easycook;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Favorites extends Fragment implements RecyclerViewClickListener {
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
        ArrayList<Integer> recipeImages = new ArrayList<Integer>();
        recipeImages.add(R.drawable.menulist);
        String[] recipeNames = new String[1];
        recipeNames[0] = "Test";
        rvAdapter = new FavoritesAdapter(recipeNames, recipeImages);
        rvAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lstFavorites.setLayoutManager(mLayoutManager);
        lstFavorites.setItemAnimator(new DefaultItemAnimator());
        lstFavorites.setAdapter(rvAdapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {

    }
}

class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> {
    private RecyclerViewClickListener clickListener;
    private String[] recipeNames;
    private ArrayList<Integer> recipeImages;

    public FavoritesAdapter(String[] recipeNames, ArrayList<Integer> recipeImages){
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
