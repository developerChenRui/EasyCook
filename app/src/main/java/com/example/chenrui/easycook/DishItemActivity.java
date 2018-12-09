package com.example.chenrui.easycook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.RotationRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DishItemActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton btnBack;
    RecyclerView reviews;
    FloatingActionButton stepBystepGuide;
    ImageView btnAdd;
    TextView numOfReviewers;
    ImageView dishImage;
    TextView dishTitle;
    TextView dishDescription;
    RotationRatingBar ratingStar;
    ImageView profile;
    TextView makerName;
    TextView cookTime;
    Recipe recipe;

    static int GETREVIEW = 1;



    //shopping list
    ArrayList<String> shoppinglist = new ArrayList<>();

    // get instructions
    ArrayList<String> instructions = new ArrayList<>();

    // retrieve data from database
     List<String> profiles = new ArrayList<>();
     List<String> reviewerNames = new ArrayList<>();
     List<Float> starNum = new ArrayList<>();
     List<String> reviewers = new ArrayList<>();
     List<String> dates = new ArrayList<>();

    ReviewSaver reviewSaver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_item);



        // get data from the previous activity
            // get the bundle from the DiscoveryFragment
        Bundle bundle = getIntent().getExtras();

           // covert bundle to recipe object
        recipe = Utils.Bundle2Recipe(bundle);
        Log.d("CHECKK2",recipe.getRecipeId());



        // initialize the components of layout
        dishImage = findViewById(R.id.dishImage);
        dishTitle = findViewById(R.id.dishTitle);
        dishDescription = findViewById(R.id.dishDescription);
        numOfReviewers = findViewById(R.id.numOfReviewer);
        ratingStar = findViewById(R.id.RatingStar);

        makerName = findViewById(R.id.makerName);
        cookTime = findViewById(R.id.cookTime);

        reviews = findViewById(R.id.recyclerReview);
           // set the components
        // TODO !!!!!!!!!
        Picasso.get().load(recipe.getRecipeImageURL()).into(dishImage);
        dishTitle.setText(recipe.getRecipeName());
        dishDescription.setText(recipe.getBriefDescription());


        // if the recipe is provided by spoonacular, retrieve the rating and # from database
        reviewSaver = new ReviewSaver();
        reviewSaver.setRecipe(recipe);

       reviewSaver.setReviewStats(recipe.getRecipeId(), new ReviewCallback() {
           @Override
           public void onCallBack() {
               ratingStar.setRating(reviewSaver.getAverageReview());
               numOfReviewers.setText("" + reviewSaver.getNumReviewers());
           }
       });
        makerName.setText(recipe.getMakerName());
        cookTime.setText(String.valueOf(recipe.getCookTime()) + " min");
          // dynamic add the ingredient checkbox
        int numOfIngredients = recipe.getIngredients().length();
              // find the place we put the checkbox
        LinearLayout ingredientLayout = findViewById(R.id.IngredientCheckbox);
        LinearLayout.LayoutParams paramsCheckBox = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
        LinearLayout.LayoutParams paramsView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        paramsCheckBox.leftMargin = 30;

        for(int i=0; i<numOfIngredients; i++) {
            StateListDrawable stateList = new StateListDrawable();
            int statePressed = android.R.attr.state_pressed;
            int stateChecked = android.R.attr.state_checked;
            stateList.addState(new int[] {-stateChecked}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.addshoppinglist)));
            stateList.addState(new int[] {stateChecked}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.successadd)));
            stateList.addState(new int[] {statePressed}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.addshoppinglist)));
            CheckBox ingredient = new CheckBox(this);
            ingredient.setButtonDrawable(stateList);

            try {
                String name = recipe.getIngredients().getJSONObject(i).getString("name");
                ingredient.setText("    " +recipe.getIngredients().getJSONObject(i).getDouble("amount")+" " +
                        recipe.getIngredients().getJSONObject(i).getString("unit") + " " +
                        recipe.getIngredients().getJSONObject(i).getString("name"));

//            try {
//                ingredient.setText("    " + recipe.getIngredients().get(i));
//            } catch(Exception e) {
//
//            }
            ingredient.setTextSize(16);
            ingredient.setTypeface(Typeface.SANS_SERIF);
            ingredient.setTextColor(Color.parseColor("#616161"));
            ingredient.setLayoutParams(paramsCheckBox);
            ingredient.setPadding(20, 20, 20, 30);
            ingredient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ingredient.isChecked()){
                        //add to the shopping list
                        Toasty.success(getBaseContext(), "Successfully add it to the shopping list!", Toast.LENGTH_LONG, true).show();
                        shoppinglist.add(name);
                    }
                }
            });
        } catch (JSONException e) {

        }

            View v = new View(this);
            v.setBackground(getResources().getDrawable(R.color.separateLine));
            v.setLayoutParams(paramsView);

            ingredientLayout.addView(ingredient);
            ingredientLayout.addView(v);
        }
          // dynamic add the instructions
        int numOfInstructions = recipe.getInstructions().length();
        LinearLayout instructionLayout = findViewById(R.id.instructions);
        LinearLayout.LayoutParams paramsTextView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTextView.leftMargin = 30;
        for(int i=0; i<numOfInstructions; i++) {
            TextView instruction = new TextView(this);
            try {
                instructions.add(recipe.getInstructions().getJSONObject(i).getString("step"));
                instruction.setText((i+1) + "   " +recipe.getInstructions().getJSONObject(i).getString("step"));
            } catch (JSONException e) {

            }

            instruction.setTextSize(16);
            instruction.setPadding(50, 20, 50, 20);
            instruction.setTextColor(Color.parseColor("#616161"));
            instructionLayout.addView(instruction);


        }

        //TODO recycler view review part --------------------------------------------------------------------------

        reviewSaver.fetchReviews(recipe.getRecipeId(), new ReviewCallback() {
            @Override
            public void onCallBack() {
                JSONArray reviewJsonArray = reviewSaver.getReviews();
                for(int i=0; i<reviewJsonArray.length(); i++) {
                    Review review = new Review();
                    try {
                        review.fromJSON(reviewJsonArray.getJSONObject(i));

                        profiles.add(review.getProfileImgURL());

                        reviewerNames.add(review.getUsername());
                        starNum.add(review.getRating());
                        reviewers.add(review.getText());
                        dates.add(review.getRelativeTime());


                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        reviews.setLayoutManager(layoutManager);
                        RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(),profiles,reviewerNames,dates,starNum,reviewers);
                        reviews.setNestedScrollingEnabled(false);
                        reviews.setAdapter(adapter);

                    }catch (Exception e) {

                    }
                }
            }
        });


        // add review to the reviews
        btnAdd = findViewById(R.id.add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DishItemActivity.this, WriteReviewActivity.class);
                startActivityForResult(i,GETREVIEW);
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
                Intent intent = new Intent();
                intent.putStringArrayListExtra("shoppingList",shoppinglist);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


        // floating action button
        stepBystepGuide = findViewById(R.id.step_by_step_btn);
        stepBystepGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DishItemActivity.this,StepByStepActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("stepBystepInstructions",recipe.getInstructions().toString());
             //   bundle.putParcelableArray("stepImages");
                i.putExtras(bundle);
                startActivity(i);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GETREVIEW && resultCode == Activity.RESULT_OK) {
            // get review text and rating
            String returnReview = data.getStringExtra("review text");
            float raing = data.getFloatExtra("review rating",0);

            // Upload to the database
            Review reviewToDatabase = new Review(Utils.username,
                    "", returnReview, raing, new Timestamp(System.currentTimeMillis()));

            Log.d("CHECKK3",recipe.getRecipeId());
            reviewSaver.addReview(recipe.getRecipeId(),reviewToDatabase.toJSON(),getBaseContext().getFilesDir());


            profiles.add("");
            reviewerNames.add(Utils.username);
            starNum.add(raing);
            reviewers.add(returnReview);
            dates.add(Utils.getRelativeTime(new Timestamp(System.currentTimeMillis())));




            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            reviews.setLayoutManager(layoutManager);
            RecyclerAdapter adapter = new RecyclerAdapter(getBaseContext(),profiles,reviewerNames,dates,starNum,reviewers);
            reviews.setNestedScrollingEnabled(false);
            reviews.setAdapter(adapter);
        }

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
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String sharedMeg = "";
           // JSONArray insArr = recipe.getInstructions().getJSONArray();
            for (int i = 0; i < instructions.size(); i++){
                    sharedMeg += "Step "+ (i+1) + ": " + instructions.get(i) + "\n";
            }
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "EasyCook Recipe of"+"  "+ recipe.getRecipeName());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharedMeg);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return(true);

        case R.id.heart:
            //add the function to perform here
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }
}
