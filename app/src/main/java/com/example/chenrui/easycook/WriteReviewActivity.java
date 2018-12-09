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

import com.willy.ratingbar.ScaleRatingBar;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class WriteReviewActivity extends AppCompatActivity {

    ImageButton btnCloseReview;
    ImageButton btnSaveReview;
    ScaleRatingBar RatingStarInReview;
    EditText editReview;
//    ImageView addImageToReview;
//    ImageView deleteImage;
    boolean addSuccessful;

    final int SELECT_IMAGE = 0;

    // for update the reviews
    RecyclerView reviews;

    // return from the photo gallery of the phone
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SELECT_IMAGE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (data != null) {
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), data.getData());
//                        addSuccessful = true;
//                        addImageToReview.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED)  {
//                Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        addSuccessful = false;
        btnCloseReview = findViewById(R.id.btnCloseReview);
        btnSaveReview = findViewById(R.id.btnSaveReview);
        RatingStarInReview = findViewById(R.id.RatingStarInReview);
        editReview = findViewById(R.id.editReview);
//        addImageToReview = findViewById(R.id.addImageToReview);
//        deleteImage = findViewById(R.id.deleteImage);

        btnCloseReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float numOfStar = RatingStarInReview.getNumStars();
//                String review = editReview.getText().toString();
                // add the review to bundle
                Intent intent = new Intent();
                intent.putExtra("review text", editReview.getText().toString());
                intent.putExtra("review rating",numOfStar);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


//        addImageToReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // open the phone photo
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
//            }
//        });
//
//
//
//        deleteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addSuccessful = false;
//                addImageToReview.setImageResource(R.drawable.add_image);
//            }
//        });

    }

//    private void updateReviews(String name, String date){//,Bitmap uploadedImage) {
//        // review part - recycler view
//
//        List<String> profiles = DishItemActivity.profiles;
//        List<String> names = DishItemActivity.reviewerNames;
//        List<String> dates = DishItemActivity.dates;
//        List<Float> starNum = DishItemActivity.starNum;
//        List<String> reviewers =DishItemActivity.reviewers;
//
////        profiles.add(R.drawable.profile);
//        names.add(name);
//        dates.add(date);
//        reviewers.add(editReview.getText().toString());
//        starNum.add(RatingStarInReview.getRating());
//
//
//        reviews = DishItemActivity.reviews;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        reviews.setLayoutManager(layoutManager);
//        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(),profiles,names,dates,starNum,reviewers);
//        reviews.setNestedScrollingEnabled(false);
//        reviews.setAdapter(adapter);
//    }


}
