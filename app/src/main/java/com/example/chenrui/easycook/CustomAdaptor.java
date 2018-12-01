package com.example.chenrui.easycook;

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

import java.util.List;

class CustomAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Recipe> recipesList;
    private Context context;
    private View v;


    public CustomAdaptor(List<Recipe> recipesList, Context context, View v){
        this.recipesList = recipesList;
        this.context = context;
        this.v = v;
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
//            cHolder.dishRB.setRating(3); /** 3 hard coding **/
//            cHolder.dishImage.setImageResource(this.ImageList.get(position).intValue());
            Picasso.get().load(this.recipesList.get(position).getRecipeImageURL()).into(cHolder.dishImage);
            Log.d("ImageCheck",this.recipesList.get(position).getRecipeImageURL());
            cHolder.dishNameLabel.setText(this.recipesList.get(position).getRecipeName());
            cHolder.likeNumLabel.setText(String.valueOf(this.recipesList.get(position).getNumOfReviewer())); /** 0 hard coding**/
//            cHolder.commentLabel.setText(this.recipesList.get(position).getBriefDescription());
            cHolder.favBar.setChecked(true);
            cHolder.favBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** modify later**/
                    if (cHolder.favBar.isChecked()) Toast.makeText(context,
                            "User likes recipe " + position, Toast.LENGTH_SHORT).show();
                    else Toast.makeText(context,
                            "User unlikes recipe " + position, Toast.LENGTH_SHORT).show();
                }
            });
            /** may have problem**/
            cHolder.userImage.setImageResource(R.drawable.profile);
            cHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,DishItemActivity.class);
                    Recipe recipe = new Recipe();
                    i.putExtras(Utils.Recipe2Bundle(recipe));
                    context.startActivity(i);
                }
            });
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

    class contentHolder extends RecyclerView.ViewHolder{
        protected TextView dishNameLabel;
        protected ImageView dishImage;
        protected ScaleRatingBar dishRB;
        private TextView likeNumLabel;
        private CheckBox favBar;
        private RoundImageView userImage;
        private TextView commentLabel;

        contentHolder(View itemView){
            super(itemView);
            dishNameLabel = itemView.findViewById(R.id.dishNameLabel);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishRB = itemView.findViewById(R.id.rBar);
            likeNumLabel = itemView.findViewById(R.id.likeNumLabel);
            favBar = itemView.findViewById(R.id.userFavourite);
            userImage = itemView.findViewById(R.id.userImage);
 //           commentLabel = itemView.findViewById(R.id.comment);
        }

    }



}