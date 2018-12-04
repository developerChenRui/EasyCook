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
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.RotationRatingBar;

import java.util.ArrayList;
import java.util.List;

public class DishItemActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton btnBack;
    static RecyclerView reviews;
    FloatingActionButton stepBystepGuide;
    ImageView btnAdd;

    ImageView dishImage;
    TextView dishTitle;
    TextView dishDescription;
    RotationRatingBar ratingStar;
    TextView numOfReviewer;
    ImageView profile;
    TextView makerName;
    TextView cookTime;


    // fake data
    static List<Integer> profiles = new ArrayList<>();
    static List<String> reviewerNames = new ArrayList<>();
    static List<Float> starNum = new ArrayList<>();
    static List<String> reviewers = new ArrayList<>();
    static  List<String> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_item);

        // get data from the previous activity
            // get the bundle from the DiscoveryFragment
        Bundle bundle = getIntent().getExtras();

           // covert bundle to recipe object
        Recipe recipe = Utils.Bundle2Recipe(bundle);



        // initialize the components of layout
        dishImage = findViewById(R.id.dishImage);
        dishTitle = findViewById(R.id.dishTitle);
        dishDescription = findViewById(R.id.dishDescription);
        ratingStar = findViewById(R.id.RatingStar);
        numOfReviewer = findViewById(R.id.numOfReviewer);
        makerName = findViewById(R.id.makerName);
        cookTime = findViewById(R.id.cookTime);
           // set the components
        // TODO !!!!!!!!!
        Picasso.get().load(recipe.getRecipeImageURL()).into(dishImage);
        dishTitle.setText(recipe.getRecipeName());
        dishDescription.setText(recipe.getBriefDescription());
        ratingStar.setRating(recipe.getRating());
//        numOfReviewer.setText(recipe.getNumOfReviewer());
        makerName.setText(recipe.getMakerName());
        cookTime.setText(recipe.getCookTime() + " min");
          // dynamic add the ingredient checkbox
        int numOfIngredients = recipe.getIngredients().size();
              // find the place we put the checkbox
        LinearLayout ingredientLayout = findViewById(R.id.IngredientCheckbox);
        LinearLayout.LayoutParams paramsCheckBox = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        LinearLayout.LayoutParams paramsView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        paramsCheckBox.leftMargin = 25;

        for(int i=0; i<numOfIngredients; i++) {
            CheckBox ingredient = new CheckBox(this);
            ingredient.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(ingredient.isChecked()){
                        //add to the shopping list
                        System.out.println("Checked");
                    }else{
                        //delete from the shopping list
                        System.out.println("Un-Checked");
                    }
                }
            });
            ingredient.setText("    " + recipe.getIngredients().get(i));
            ingredient.setTextSize(20);
            ingredient.setLayoutParams(paramsCheckBox);

            View v = new View(this);
            v.setBackground(getResources().getDrawable(R.color.separateLine));
            v.setLayoutParams(paramsView);

            ingredientLayout.addView(ingredient);
            ingredientLayout.addView(v);
        }
          // dynamic add the instructions
        int numOfInstructions = recipe.getInstructions().size();
        LinearLayout instructionLayout = findViewById(R.id.instructions);
        LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTextView.leftMargin = 30;
        for(int i=0; i<numOfInstructions; i++) {
            TextView instruction = new TextView(this);
            instruction.setText("   " + (i+1) + ". " +recipe.getInstructions().get(i));
            instruction.setTextSize(20);
            instruction.setTextColor(getResources().getColor(R.color.black));
            instruction.setPadding(0,16,0,0);
            instructionLayout.addView(instruction);

        }

        //TODO recycler view review part --------------------------------------------------------------------------

        // initialize fake data
        profiles.add(R.drawable.profile);
        profiles.add(R.drawable.profile);

        reviewerNames.add("chen");
        reviewerNames.add("rui");

        starNum.add((float)1.0);
        starNum.add((float)4.0);

        reviewers.add("gooooooooooooooooooooooookokokkkokokokokokkd");
        reviewers.add("baaaaaaaaad");

        dates.add("1 min ago");
        dates.add("1 day ago");

        // review part - recycler view

        reviews = findViewById(R.id.recyclerReview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reviews.setLayoutManager(layoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(),profiles,reviewerNames,dates,starNum,reviewers);
        reviews.setNestedScrollingEnabled(false);
        reviews.setAdapter(adapter);

        // add review to the reviews
        btnAdd = findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DishItemActivity.this, WriteReviewActivity.class);
                startActivity(i);
            }
        });

        //TODO recycler view review part --------------------------------------------------------------------------




        // set the toolbar in the top
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


        // floating action button
        stepBystepGuide = findViewById(R.id.step_by_step_btn);
        stepBystepGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DishItemActivity.this,StepByStepActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("stepBystepInstructions",recipe.getInstructions());
             //   bundle.putParcelableArray("stepImages");
                i.putExtras(bundle);
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
