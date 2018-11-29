package com.example.chenrui.easycook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.willy.ratingbar.BaseRatingBar;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {
    List<Integer> profiles;
    List<String> names;
    List<String> mdate;
    List<Float> starNum;
    List<String> review;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ReviewProfile;
        TextView reviewerName;
        TextView postDate;
        BaseRatingBar RatingStarInReview;
        TextView oneReview;



        public ViewHolder(View view) {
            super(view);
            ReviewProfile = view.findViewById(R.id.ReviewProfile);
            reviewerName = view.findViewById(R.id.reviewerName);
            postDate = view.findViewById(R.id.postDate);
            RatingStarInReview = view.findViewById(R.id.RatingStarInReview);
            oneReview = view.findViewById(R.id.oneReview);

        }

    }

    public RecyclerAdapter(Context aContext, List<Integer> profiles, List<String> names, List<String> date, List<Float> starNum, List<String> review) {
        this.profiles = profiles;
        this.names = names;
        this.mdate = date;
        this.starNum = starNum;
        this.review = review;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item_layout, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.oneReview.setText(review.get(i));
        holder.RatingStarInReview.setRating(starNum.get(i));
        holder.postDate.setText(""+mdate.get(i));
        holder.reviewerName.setText(names.get(i));
    }

    @Override
    public int getItemCount() {
        return review.size();
    }
}
