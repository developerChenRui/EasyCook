package com.example.chenrui.easycook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

class CustomAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Recipe> recipesList;
    private Context context;

    public static int justOpenPosition;
    float rating = 0;


    public void changeRecipeList(int position, float rating, int num) {
        Recipe recipe = recipesList.get(position);
        recipe.setNumOfReviewer(num);
        recipe.setRating(rating);
        this.rating = rating;
        notifyItemChanged(position);
    }


    public CustomAdaptor(List<Recipe> recipesList, Context context){
        this.recipesList = recipesList;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {
        return recipesList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        /** modify latter**/
        /** setOnRatingBarChangeListener **/
        if (holder instanceof contentHolder){
            contentHolder cHolder = (contentHolder) holder;
            cHolder.dishRB.setRating(rating); /** 3 hard coding **/
//            cHolder.dishImage.setImageResource(this.ImageList.get(position).intValue());
            if (this.recipesList.get(position).getRecipeImageURL().equals("")) {
                cHolder.dishImage.setImageResource(R.drawable.hamburger);
            } else {
                Picasso.get().load(this.recipesList.get(position).getRecipeImageURL()).into(cHolder.dishImage);
            }
            Log.d("ImageCheck",this.recipesList.get(position).getRecipeImageURL());
            cHolder.dishNameLabel.setText(this.recipesList.get(position).getRecipeName());
            cHolder.likeNumLabel.setText(String.valueOf(this.recipesList.get(position).getNumOfReviewer())); /** 0 hard coding**/
//            cHolder.commentLabel.setText(this.recipesList.get(position).getBriefDescription());
            cHolder.favBar.setChecked(true);
            cHolder.userImage.setImageResource(R.drawable.profile);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_panel, parent, false);
        return new CustomAdaptor.contentHolder(view);
    }



    @Override
    public int getItemCount() {
        return recipesList.size();
    }

    class contentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView dishNameLabel;
        protected ImageView dishImage;
        protected ScaleRatingBar dishRB;
        private TextView likeNumLabel;
        private CheckBox favBar;
        private RoundImageView userImage;

        contentHolder(View itemView){
            super(itemView);
            dishNameLabel = itemView.findViewById(R.id.dishNameLabel);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishRB = itemView.findViewById(R.id.rBar);
            likeNumLabel = itemView.findViewById(R.id.likeNumLabel);
            favBar = itemView.findViewById(R.id.userFavourite);
            userImage = itemView.findViewById(R.id.userImage);
            favBar.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.userFavourite:
                    if (favBar.isChecked()){
                        Toast.makeText(context,
                                "User likes recipe " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        try{
                            Utils.user.setFavoriteRecipes(Utils.user.getFavoriteRecipes()
                                    .put(recipesList.get(getAdapterPosition()).getRecipeId()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    else Toast.makeText(context,
                            "User unlikes recipe " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    JSONArray idArr = Utils.user.getFavoriteRecipes();
                    for (int i = 0; i < idArr.length(); i++){
                        try{
                            if (idArr.getString(i).equals(recipesList.get(getAdapterPosition()).getRecipeId())){
                                idArr.remove(i);
                                break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Utils.user.setFavoriteRecipes(idArr);
                    }

                    break;

                default:
                    Intent i = new Intent(context,DishItemActivity.class);
                    justOpenPosition = getAdapterPosition();
                    Utils.recipeIdSearch(recipesList.get(getAdapterPosition()).getRecipeId(), new AsyncData() {
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
                    break;
            }
        }
    }



}