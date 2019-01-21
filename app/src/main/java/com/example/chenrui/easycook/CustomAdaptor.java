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


/***
 * CustomAdaptor
 *
 * Used in Discovery fragment to store recipes
 */
class CustomAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Recipe> recipesList;
    private Context context;
    JSONArray farArr = Utils.user.getFavoriteRecipes();

    public static int justOpenPosition;
    float rating = 0;


    /***
     * changerecipeList
     *
     * @param position     int    Position in the list
     * @param rating       float  Average rating of the recipe
     * @param num          int    Number of reviewers of the recipe
     ***/
    public void changeRecipeList(int position, float rating, int num) {
        if(position <= recipesList.size()-1) {
            Recipe recipe = recipesList.get(position);
            recipe.setNumOfReviewer(num);
            recipe.setRating(rating);
            this.rating = rating;
            notifyItemChanged(position);
        }
    }


    public CustomAdaptor(List<Recipe> recipesList, Context context){
        this.recipesList = recipesList;
        this.context = context;

    }

    @Override
    public int getItemViewType(int position) {
        return recipesList.size();
    }


    /***
     * onBindViewHolder
     *
     * @param holder       RecyclerView.ViewHolder  The recipe panel in the adapter
     * @param position     final int                Position of the recipe in the adapter
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        /** modify later**/
        /** setOnRatingBarChangeListener **/
        if (holder instanceof contentHolder){
            contentHolder cHolder = (contentHolder) holder;
;
            // If the recipe has a picture, use the picture, otherwise use the default
            if (this.recipesList.get(position).getRecipeImageURL().equals("")) {
                cHolder.dishImage.setImageResource(R.drawable.hamburger);
            } else {
                Picasso.get().load(this.recipesList.get(position).getRecipeImageURL()).into(cHolder.dishImage);
            }
            Log.d("ImageCheck",this.recipesList.get(position).getRecipeImageURL());

            // Set username of maker of the recipe
            cHolder.username.setText(this.recipesList.get(position).getMakerName());

            // Set the review stats of the recipe (number of reviewers and average rating)
            ReviewSaver reviewSaver = new ReviewSaver();
            reviewSaver.setReviewStats(this.recipesList.get(position).getRecipeId(), new ReviewCallback() {
                @Override
                public void onCallBack() {
                    cHolder.dishRB.setRating(reviewSaver.getAverageReview());
                    cHolder.likeNumLabel.setText("" + reviewSaver.getNumReviewers());
                }
            });

            cHolder.dishNameLabel.setText(this.recipesList.get(position).getRecipeName());

            // If the recipe is favorited by the user, mark the favorite icon
            String recipeId = this.recipesList.get(position).getRecipeId();
            try{
                for (int i = 0; i < farArr.length(); i++){
                    String id = farArr.getString(i);
                    if (recipeId.equals(id)){
                        cHolder.favBar.setChecked(true);
                        break;
                    } else {
                        cHolder.favBar.setChecked(false);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            // Set the recipe maker's profile image if it exists
            if (this.recipesList.get(position).getProfileURL().equals("")){
                cHolder.userImage.setImageResource(R.drawable.profile);
            } else {
                Picasso.get().load(this.recipesList.get(position).getProfileURL()).into(cHolder.userImage);
            }
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


    /***
     * contentholder
     *
     * Represents each dish as a panel in the CustomAdaptor
     ***/
    class contentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView dishNameLabel;
        protected ImageView dishImage;
        protected ScaleRatingBar dishRB;
        private TextView likeNumLabel;
        private CheckBox favBar;
        private RoundImageView userImage;
        private TextView username;

        contentHolder(View itemView){
            super(itemView);
            dishNameLabel = itemView.findViewById(R.id.dishNameLabel);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishRB = itemView.findViewById(R.id.rBar);
            likeNumLabel = itemView.findViewById(R.id.likeNumLabel);
            favBar = itemView.findViewById(R.id.userFavourite);
            userImage = itemView.findViewById(R.id.userImage);
            username = itemView.findViewById(R.id.username);
            favBar.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                // If the favorite button is clicked, add/remove recipe from favorites list
                case R.id.userFavourite:
                    if (favBar.isChecked()){
                        Toast.makeText(context,
                                "User likes recipe " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        try{
                            Utils.user.addFavorite(recipesList.get(getAdapterPosition()).getRecipeId());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else{
                        Toast.makeText(context, "User unlikes recipe " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        Utils.user.removeFavorite(recipesList.get(getAdapterPosition()).getRecipeId());
                    }
                    break;

                default:

                    // Recipe from spoonacular
                    if (isDigit(recipesList.get(getAdapterPosition()).getRecipeId())) {
                        Intent i = new Intent(context,DishItemActivity.class);
                        i.putExtra("id",0);
                        justOpenPosition = getAdapterPosition();

                        // Use the recipe ID to query spoonacular api
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

                    // User created recipe
                    } else {

                        Intent i = new Intent(context,DishItemActivity.class);
                        justOpenPosition = getAdapterPosition();
                        i.putExtras(Utils.Recipe2Bundle(recipesList.get(getAdapterPosition())));
                        ((Activity)context).startActivityForResult(i,NavigateActivity.GETINGREDIENTS);
                    }
                    break;
            }
        }
    }

    /***
     * isDigit
     *
     * @param id     String   Recipe ID
     * @return       boolean  Output if the recipe ID is only digits, it must be from spoonacular, otherwise a user created recipe
     ***/
    private boolean isDigit(String id){
        try{
            Double.parseDouble(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }



}