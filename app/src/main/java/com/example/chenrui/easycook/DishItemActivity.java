package com.example.chenrui.easycook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DishItemActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton btnBack;
    static RecyclerView reviews;
    FloatingActionButton stepBystepGuide;
    ImageView btnAdd;


    // fake data
    static List<Integer> profiles = new ArrayList<>();
    static List<String> names = new ArrayList<>();
    static List<Float> starNum = new ArrayList<>();
    static List<String> reviewers = new ArrayList<>();
    static  List<String> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_item);

        // initialize fake data
        profiles.add(R.drawable.profile);
        profiles.add(R.drawable.profile);

        names.add("chen");
        names.add("rui");

        starNum.add((float)1.0);
        starNum.add((float)4.0);

        reviewers.add("gooooooooooooooooooooooookokokkkokokokokokkd");
        reviewers.add("baaaaaaaaad");

        dates.add("1 min ago");
        dates.add("1 day ago");



        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // hide the title of actionbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // initialize the back button of action bar
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // review part - recycler view

        reviews = findViewById(R.id.recyclerReview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reviews.setLayoutManager(layoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(),profiles,names,dates,starNum,reviewers);
        reviews.setNestedScrollingEnabled(false);
        reviews.setAdapter(adapter);

        // floating action button
        stepBystepGuide = findViewById(R.id.step_by_step_btn);
        stepBystepGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DishItemActivity.this,StepByStepActivity.class);
                startActivity(i);
            }
        });

        // add review to the reviews
        btnAdd = findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DishItemActivity.this, WriteReviewActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dish_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.share:
            //add the function to perform here
            return(true);
        case R.id.heart:
            //add the function to perform here
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }
}
