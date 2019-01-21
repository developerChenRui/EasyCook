package com.example.chenrui.easycook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

/***
 * RecyclerAdapter
 *
 * Stores information for the reviews for a recipe
 ***/
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {
    List<String> names;
    List<String> emails;
    List<String> profiles;
    List<String> review;
    List<Float> starNum;
    List<Boolean> likes;
    List<String> mdate;
    String recipeID;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ReviewProfile;
        TextView reviewerName;
        TextView postDate;
        BaseRatingBar RatingStarInReview;
        TextView oneReview;
        ScaleRatingBar liked;
        String reviewerEmail;


        public ViewHolder(View view, ReviewLikeCallback callback) {
            super(view);
            ReviewProfile = view.findViewById(R.id.ReviewProfile);
            reviewerName = view.findViewById(R.id.reviewerName);
            postDate = view.findViewById(R.id.postDate);
            RatingStarInReview = view.findViewById(R.id.RatingStarInReview);
            oneReview = view.findViewById(R.id.oneReview);
            liked = view.findViewById(R.id.liked);

            // User has liked a review
            liked.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
                    if (v == 0.0) {
                        callback.onCallback(false);
                    } else {
                        callback.onCallback(true);
                    }
                }
            });

        }

    }

    public RecyclerAdapter(Context aContext, List<String> profiles, List<String> names, List<String> emails, List<String> date, List<Float> starNum, List<String> review, List<Boolean> likes, String recipeID) {
        this.profiles = profiles;
        this.names = names;
        this.emails = emails;
        this.mdate = date;
        this.starNum = starNum;
        this.review = review;
        this.likes = likes;
        this.recipeID = recipeID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item_layout, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view, new ReviewLikeCallback() {
            @Override
            public void onCallback(boolean liked) {
                likes.set(i,liked);
                ReviewSaver reviewSaver = new ReviewSaver();
                reviewSaver.changeUserLike(recipeID,emails.get(i),Utils.user.getEmail(),liked);

            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        if(profiles.get(i).isEmpty()) {
            holder.ReviewProfile.setImageResource(R.drawable.profile);
        } else {
            Picasso.get().load(profiles.get(i)).transform(new PicassoCircleTransformation()).into(holder.ReviewProfile);
        }
        holder.oneReview.setText(review.get(i));
        holder.RatingStarInReview.setRating(starNum.get(i));
        holder.postDate.setText(""+mdate.get(i));
        holder.reviewerName.setText(names.get(i));
        holder.reviewerEmail = emails.get(i);

        if (likes.get(i)) {
            holder.liked.setRating((float)1.0);
        }
    }

    @Override
    public int getItemCount() {
        return review.size();
    }
}
