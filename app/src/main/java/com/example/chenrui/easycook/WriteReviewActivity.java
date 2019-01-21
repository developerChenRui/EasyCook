package com.example.chenrui.easycook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/***
 * WriteReviewActivity
 *
 * Create new reviews
 ***/
public class WriteReviewActivity extends AppCompatActivity {

    ImageButton btnCloseReview;
    ImageButton btnSaveReview;
    ScaleRatingBar RatingStarInReview;
    EditText editReview;
    boolean addSuccessful;

    final int SELECT_IMAGE = 0;

    // for update the reviews
    RecyclerView reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        addSuccessful = false;
        btnCloseReview = findViewById(R.id.btnCloseReview);
        btnSaveReview = findViewById(R.id.btnSaveReview);
        RatingStarInReview = findViewById(R.id.RatingStarInReview);
        editReview = findViewById(R.id.editReview);


        btnCloseReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float numOfStar = RatingStarInReview.getRating();

                Intent intent = new Intent();
                intent.putExtra("review text", editReview.getText().toString());
                intent.putExtra("review rating",numOfStar);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


    }




}
