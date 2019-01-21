package com.example.chenrui.easycook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/***
 * CreateActivity
 *
 * Users create new recipes through this activity
 ***/
public class CreateActivity extends AppCompatActivity implements TextWatcher, View.OnKeyListener{

    //    private Toolbar toolbar;
    private List<LocalMedia> selectList = new ArrayList<>();
    private List<LocalMedia> mediaList = new ArrayList<>();
    private GridImageAdapter adapter;
    private static RecyclerView ingRV;
    private static RecyclerView insRV;
    private static IngRecycleAdapter ingAdapter;
    private static InsRecycleAdapter insAdapter;
    private static List<Integer> ingList = new ArrayList<Integer>();
    private static List<Integer> insList = new ArrayList<Integer>();
    private static Button iv_add;
    private static Button step_add;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private static int themeId = R.style.picture_white_style;
    private int chooseMode = PictureMimeType.ofAll();

    private static EditText enterName;
    private static EditText time;

    private ArrayList<String> desTags = new ArrayList<>();
    private EditText etTags;
    private static final int maxLength = 8;
    String tag = "";
    private Recipe recipe = new Recipe();

    private JSONArray tags = new JSONArray();

    private int tagsCnt = 0;

    private int stepNum = 0;

    public RecipesFragment favoriteFragment;

    List<String> source = new ArrayList<>();
    private static final boolean isChinese2English = false;

    private static final int STEP_PIC = 222;
    private int length = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        initIngView();
        initIngRecycle();
        initInsView();
        initInsRecycle();

        enterName = (EditText) findViewById(R.id.enterName);
        time = (EditText) findViewById(R.id.time);
        etTags = (EditText) findViewById(R.id.et_tags);

        etTags.addTextChangedListener(this);
        etTags.setOnKeyListener(this);


        ImageView cancel = (ImageView) findViewById(R.id.cancel);
        ImageView submit = (ImageView) findViewById(R.id.up);
        ImageView check = (ImageView) findViewById(R.id.check);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(CreateActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(CreateActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(1);
        recyclerView.setAdapter(adapter);

        // Pick cover image
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PictureSelector.create(CreateActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                if (selectList.size() > 0) {
                    Log.i("image", "click to select");
                    PictureSelector.create(CreateActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                }
            }
        });

        // Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateActivity.this, NavigateActivity.class);
                i.putExtra("id", 1);
                startActivity(i);
                finish();
            }
        });

        // Create private recipe
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // first input error check, only need to check last step
                ArrayList<String> amounts = ingAdapter.getAmounts();
                ArrayList<String> units = ingAdapter.getUnits();
                ArrayList<String> ingres = ingAdapter.getIngredients();

                if(amounts.size()==0 || ingres.size() == 0 ||
                        amounts.get(amounts.size()-1).equals("") ||
                        ingres.get(ingres.size()-1).equals("") ) {
                    Toasty.error(CreateActivity.this, "Please input ingredient", Toast.LENGTH_SHORT).show();
                    return;
                } else if(!amounts.get(amounts.size()-1).matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    Toasty.error(CreateActivity.this, "Please input a number for amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> pathList = insAdapter.getPaths();
                ArrayList<String> detailList = insAdapter.getDetail();

                // Check if there are any instructions
                if(insAdapter.getDetailCount() == 0 ||
                        insAdapter.getInsImageCount() == 0
                        || pathList.get(pathList.size()-1).equals("") ||
                        detailList.get(detailList.size()-1).equals("")) {
                    Toasty.error(CreateActivity.this,"Please input instruction",Toast.LENGTH_SHORT).show();
                    return;
                }



                String path = adapter.getPath();

                // Make tags
                onSetspan();


                if(enterName.getText().toString().equals("") ||
                        tags == null ||
                        tags.length() == 0 ||
                        time.getText().toString().equals("") ||
                        path == null || insAdapter.getInsImageCount() == 0 ||
                        insAdapter.getDetailCount() == 0){

                    if (alertDialog != null && !alertDialog.isShowing()) {
                        alertDialog.show();
                        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
                        alertDialog.getWindow().setAttributes(params);
                    } else {
                        Toasty.error(getBaseContext(),"Invalid recipe. Please try again with a recipe name, picture, and description.",Toast.LENGTH_SHORT).show();
                    }

                } else {


                    String imageName = "coverImg_"
                            + Utils.user.getEmail().replaceAll("@", "_")
                            .replaceAll(" ", "_").replace('.','_')
                            + enterName.getText().toString().replace(' ','_');

                    // disable the screen
                    setContentView(R.layout.transparent_view);
                    getWindow().getDecorView().findViewById(android.R.id.content).setClickable(false);


                    // Get bitmap of cover image
                    Bitmap bitmap = adapter.getBitmap();
                    ImageSaver imageSaver = new ImageSaver();

                    // Push cover image to cloud storage
                    imageSaver.pushImage(imageName, bitmap, getBaseContext().getFilesDir(), new ImageCallback() {


                                @Override
                                public void onCallback(String imageURL) {
                                    Log.i("imageURL", "success" + imageURL);
                                    System.out.print("success");
                                    getInstructions(new RecipeCallback() {
                                        @Override
                                        public void onCallBack(JSONArray value) {
                                            try {

                                                String userName = Utils.user.getUsername();
                                                String recipeName = enterName.getText().toString();
                                                String recipeID = enterName.getText().toString().replace(' ','_').replace('@','_').replace(',','_').replace('.','_') + "-" + Utils.user.getCleanEmail();
                                                System.out.println("recipe " + imageURL + " "  + recipeID + " " + userName);
                                                JSONArray instructions = value;
                                                JSONArray ingredients = getIngredients();
                                                System.out.println("instruction: " + instructions.toString());
                                                System.out.println("ingredients: " + ingredients.toString());
                                                JSONArray tags = getTags();
                                                System.out.println("desTags: " + desTags.toString());
                                                System.out.println("etTags: " + etTags.getText().toString());
                                                System.out.println("tags: " + tags.toString());

                                                // Create recipe object
                                                recipe.setMakerName(userName);
                                                recipe.setBriefDescription("");
                                                recipe.setRating(0);
                                                recipe.setProfileURL(Utils.user.getProfileImgURL());
                                                recipe.setNumOfReviewer(0);
                                                recipe.setRecipeImageURL(imageURL);
                                                recipe.setCookTime(Integer.parseInt(time.getText().toString()));
                                                recipe.setRecipeId(recipeID);
                                                recipe.setInstructions(instructions);
                                                recipe.setIngredients(ingredients);
                                                recipe.setRecipeName(recipeName);
                                                recipe.setTags(tags);

                                                System.out.format("Filename: %s%n",recipe.getRecipeId());

                                                // Add recipe to private recipes
                                                Utils.user.addPrivateRecipe(recipe);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Intent i = new Intent(CreateActivity.this, NavigateActivity.class);
                                            i.putExtra("id", 1);
                                            startActivity(i);

                                            //return to MyRecipes
                                            Toasty.success(CreateActivity.this, "Submitted!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });

                                }
                            }
                    );
                }

            }

        });


        // Create public recipe
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // first input error check, only need to check last step
                ArrayList<String> amounts = ingAdapter.getAmounts();
                ArrayList<String> units = ingAdapter.getUnits();
                ArrayList<String> ingres = ingAdapter.getIngredients();

                if(amounts.size()==0 || ingres.size() == 0 ||
                        amounts.get(amounts.size()-1).equals("") ||
                        ingres.get(ingres.size()-1).equals("") ) {
                    Toasty.error(CreateActivity.this, "Please input ingredient", Toast.LENGTH_SHORT).show();
                    return;
                } else if(!amounts.get(amounts.size()-1).matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    Toasty.error(CreateActivity.this, "Please input a number for amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> pathList = insAdapter.getPaths();
                ArrayList<String> detailList = insAdapter.getDetail();

                if(insAdapter.getDetailCount() == 0 ||
                        insAdapter.getInsImageCount() == 0
                        || pathList.get(pathList.size()-1).equals("") ||
                        detailList.get(detailList.size()-1).equals("")) {
                    Toasty.error(CreateActivity.this,"Please input instruction",Toast.LENGTH_SHORT).show();
                    return;
                }



                String path = adapter.getPath();

                // Create tags
                onSetspan();

                if(enterName.getText().toString().equals("") ||
                        tags == null ||
                        tags.length() == 0 ||
                        time.getText().toString().equals("") ||
                        path == null || insAdapter.getInsImageCount() == 0 ||
                        insAdapter.getDetailCount() == 0){

                    if (alertDialog != null && !alertDialog.isShowing()) {
                        alertDialog.show();
                        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
                        alertDialog.getWindow().setAttributes(params);
                    } else {
                        Toasty.error(getBaseContext(),"Invalid recipe. Please try again with a recipe name, picture, and description.",Toast.LENGTH_SHORT).show();
                    }

                } else {

                    String imageName = "coverImg_"
                            + Utils.user.getEmail().replaceAll("@", "_")
                            .replaceAll(" ", "_").replace('.','_')
                            + enterName.getText().toString().replace(' ','_');
                    Log.i("path", path);
                    Bitmap bitmap = adapter.getBitmap();

                    // disable the screen
                    setContentView(R.layout.transparent_view);
                    getWindow().getDecorView().findViewById(android.R.id.content).setClickable(false);
                    ImageSaver imageSaver = new ImageSaver();
                    imageSaver.pushImage(imageName, bitmap, getBaseContext().getFilesDir(), new ImageCallback() {

                                @Override
                                public void onCallback(String imageURL) {
                                    Log.i("imageURL", "success" + imageURL);
                                    System.out.print("success");
                                    getInstructions(new RecipeCallback() {
                                        @Override
                                        public void onCallBack(JSONArray value) {
                                            try {

                                                String userName = Utils.user.getUsername();
                                                String recipeName = enterName.getText().toString();
                                                String recipeID = enterName.getText().toString().replace(' ','_').replace('@','_').replace(',','_').replace('.','_') + "-" + Utils.user.getCleanEmail();
                                                System.out.println("recipe " + imageURL + " "  + recipeID + " " + userName);
                                                JSONArray instructions = value;
                                                JSONArray ingredients = getIngredients();
                                                System.out.println("instruction: " + instructions.toString());
                                                System.out.println("ingredients: " + ingredients.toString());
                                                JSONArray tags = getTags();
                                                System.out.println("desTags: " + desTags.toString());
                                                System.out.println("etTags: " + etTags.getText().toString());
                                                System.out.println("tags: " + tags.toString());

                                                // Create recipe object
                                                recipe.setMakerName(userName);
                                                recipe.setBriefDescription("");
                                                recipe.setRating(0);
                                                recipe.setProfileURL(Utils.user.getProfileImgURL());
                                                recipe.setNumOfReviewer(0);
                                                recipe.setRecipeImageURL(imageURL);
                                                recipe.setCookTime(Integer.parseInt(time.getText().toString()));
                                                recipe.setRecipeId(recipeID);
                                                recipe.setInstructions(instructions);
                                                recipe.setIngredients(ingredients);
                                                recipe.setRecipeName(recipeName);
                                                recipe.setTags(tags);

                                                System.out.format("Filename: %s%n",recipe.getRecipeId());

                                                // Add recipe to user's public recipes
                                                Utils.user.addPublicRecipe(recipeID);
                                                RecipeSaver recipeSaver = new RecipeSaver();
                                                recipeSaver.setRecipe(recipe);
                                                recipeSaver.pushRecipe(getBaseContext().getFilesDir());
                                                Log.i("PUSH", "success");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Intent i = new Intent(CreateActivity.this, NavigateActivity.class);
                                            i.putExtra("id", 1);

                                            startActivity(i);

                                            //return to MyRecipes
                                            Toasty.success(CreateActivity.this, "Submitted!", Toast.LENGTH_SHORT).show();

                                            finish();
                                        }
                                    });

                                }
                            }
                    );
                }}
        });

        // Add ingredient
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // first check last one if valid
                ArrayList<String> amounts = ingAdapter.getAmounts();
                ArrayList<String> units = ingAdapter.getUnits();
                ArrayList<String> ingres = ingAdapter.getIngredients();

                if(amounts.size()==0 || ingres.size() == 0 ||
                        amounts.get(amounts.size()-1).equals("") ||
                        ingres.get(ingres.size()-1).equals("") ) {
                    Toasty.error(CreateActivity.this, "Please finish the last one first", Toast.LENGTH_SHORT).show();
                } else if(!amounts.get(amounts.size()-1).matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    Toasty.error(CreateActivity.this, "Please input a number for amount", Toast.LENGTH_SHORT).show();
                } else {
                    ingAdapter.addData(ingList.size());
                }
            }
        });

        // Add instruction
        step_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.normal(CreateActivity.this, "add clicked", Toast.LENGTH_SHORT).show();
//                resetMedia();

                ArrayList<String> pathList;
                pathList = insAdapter.getPaths();
                ArrayList<String> detailList;
                detailList = insAdapter.getDetail();

                // first check last step is complete or not
                if(insAdapter.getDetailCount() == 0 || insAdapter.getInsImageCount() == 0
                        || pathList.get(pathList.size()-1).equals("") ||
                        detailList.get(detailList.size()-1).equals("")) {
                    Toasty.error(CreateActivity.this,"Please finish the last step first",Toast.LENGTH_SHORT).show();
                } else {
                    insAdapter.addData(insList.size());
                }
            }
        });
    }

    private void initIngRecycle() {
        LinearLayoutManager ingLinearLayoutManager = new LinearLayoutManager(this);
        ingRV.setLayoutManager(ingLinearLayoutManager);
        ingList = ingInitData();
        ingAdapter = new IngRecycleAdapter(CreateActivity.this, ingList);
        ingRV.setAdapter(ingAdapter);
        ingRV.setItemAnimator(new DefaultItemAnimator());

    }

    protected ArrayList<Integer> ingInitData() {
        ArrayList<Integer> mDatas = new ArrayList<Integer>();
        for (int i = 0; i < 1; i++) {
            mDatas.add(R.layout.item_layout);
        }
        return mDatas;
    }


    private void initIngView() {
        iv_add = (Button) findViewById(R.id.addIngredient);
        ingRV = (RecyclerView) findViewById(R.id.ingredients);
    }

    /***
     * getInstructions
     *
     * @param callback     RecipeCallback  Gets a JSONArray back. Should be the instructions with urls
     *
     * Gets instructions back through the callback
     ***/
    private void  getInstructions(RecipeCallback callback) {
        JSONArray instructions = new JSONArray();

        int count = insAdapter.getItemCount();
        ArrayList<String> pathList;
        pathList = insAdapter.getPaths();
        ArrayList<String> detailList;
        detailList = insAdapter.getDetail();
        for(int i = 0; i < count; i++) {
            JSONObject step = new JSONObject();

            String imageName = enterName.getText().toString() + "_" + i;
            if (pathList.size() == 0) {
                callback.onCallBack(new JSONArray());
                return;
            }
            String path = pathList.get(i);
            String detail = detailList.get(i);
            System.out.println("PathList" + path);
            Bitmap bitmap = insAdapter.getBitmap(path);
            Log.i("bitmapSave", "" + bitmap.getByteCount());

            // Add step and image to instructions
            try {
                step.put("step", detail);
                step.put("stepimg", path);
                System.out.print("step " + i + detail);
                System.out.print("step " + i + path);
                instructions.put(i, step);

            } catch (JSONException e) {

            }

            // Push step image to cloud storage and get url back
            ImageSaver imageSaver = new ImageSaver();
            imageSaver.pushImage(imageName, bitmap, getBaseContext().getFilesDir(),i, new InstructionsCallback() {
                @Override
                public void onCallback(String imageURL,int i) {
                    //                   Log.i("step", "success" + imageURL);
                    System.out.print("step upload success");
                    try {
                        JSONObject temp = instructions.getJSONObject(i);
                        temp.put("image", imageURL);
                        Log.i("GETINSTRUCTIONS", "success" + i);
//                        instructions.remove(j);
                        instructions.put(i,temp);
                        stepNum++;
                        if(stepNum >= count) {
                            callback.onCallBack(instructions);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        return;
    }


    /***
     * getingredients
     *
     * @return JSONArray      The list of ingredients (JSONObjects with tags "name", "amount", and "unit"
     * @throws JSONException  Uses JSONObjects/JSONArrays
     *
     * Get JSONArray of ingredients
     */
    private JSONArray getIngredients() throws JSONException {
        JSONArray ingredients = new JSONArray();
        int ingredientCnt = ingAdapter.getItemCount();

        ArrayList<String> amounts = ingAdapter.getAmounts();
        ArrayList<String> units = ingAdapter.getUnits();
        ArrayList<String> ingres = ingAdapter.getIngredients();

        if (ingres.size() == 0) {
            return new JSONArray();
        }


        for(int i = 0; i < ingredientCnt; i ++) {


            JSONObject content = new JSONObject();

            Float amount = 0.0f;
            String unit = " ";
            String ingre = "";


            amount = Float.parseFloat(amounts.get(i));
            try {
                unit = units.get(i);
            }catch (Exception e) {

            }
            ingre = ingres.get(i);

            content.put("amount", amount);
            content.put("unit", unit);
            content.put("name", ingre);

            ingredients.put(i, content);
        }

        return ingredients;
    }

    private JSONArray getTags() throws JSONException {
        tags = new JSONArray();
        for(int i = 0; i < tagsCnt; i++) {
            tags.put(i, desTags.get(i));
        }
        String[] temp = desTags.get(desTags.size() - 1).split("  ");
        for(int i = 0; i < temp.length; i++) {
            tags.put(i, temp[i]);
        }
        return tags;
    }



    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        View view = View.inflate(CreateActivity.this, R.layout.warning_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);

        Button back = (Button) view.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog = builder.create();

    }

    private void initInsRecycle() {
        LinearLayoutManager insLinearLayoutManager = new LinearLayoutManager(this);
        insRV.setLayoutManager(insLinearLayoutManager);
        insList = insInitData();

        insAdapter = new InsRecycleAdapter(CreateActivity.this, insList, addPicClickListener);
        insAdapter.setList(mediaList);
        insAdapter.setSelectMax(1);
        insRV.setAdapter(insAdapter);


        insRV.setItemAnimator(new DefaultItemAnimator());
    }

    InsRecycleAdapter.onAddPicClickListener addPicClickListener = new InsRecycleAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            mediaList.clear();
            gallery(mediaList, STEP_PIC);
        }
    };

    private void initInsView() {
        step_add = (Button) findViewById(R.id.addStep);
        insRV = (RecyclerView) findViewById(R.id.steps);
    }

    protected ArrayList<Integer> insInitData() {
        ArrayList<Integer> mDatas = new ArrayList<Integer>();
        for (int i = 0; i < 1; i++) {
            mDatas.add(R.layout.instruction_layout);
        }
        return mDatas;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        Log.i("imageupdate", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
                case STEP_PIC:
                    mediaList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : mediaList) {
                        Log.i("imageupdate", media.getPath());

                        insAdapter.photo(media);
                    }
//                    insAdapter.setList(mediaList);
//                    insAdapter.notifyDataSetChanged();
                    break;
            }
        }

        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(CreateActivity.this);
                } else {
                    Toasty.normal(CreateActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });


    }


    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            gallery(selectList, PictureConfig.CHOOSE_REQUEST);
        }
    };

    private void gallery(List<LocalMedia> list, int requestCode) {
        PictureSelector.create(CreateActivity.this)
                .openGallery(chooseMode)
                .theme(R.style.picture_white_style)
                .maxSelectNum(1)
                .minSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)
                .isCamera(true)
                .isZoomAnim(true)
                .enableCrop(true)
                .compress(true)
                .synOrAsy(true)
                .glideOverride(100, 100)
                .withAspectRatio(1, 1)
                .hideBottomControls(true)
                .isGif(false)
                .freeStyleCropEnabled(true)
                .circleDimmedLayer(false)
                .showCropFrame(true)
                .showCropGrid(true)
                .openClickSound(false)
                .selectionMedia(list)
                .minimumCompressSize(100)
                .forResult(requestCode);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        length = charSequence.length();

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(length == s.length() || s.length() == 0) {
            return;
        }
        String content = s.toString();
        String last = content.substring(content.length() - 1, content.length());

        if(" ".equals(last)) {
            onSetspan();
            this.tagsCnt += 1;
        }
    }


    /***
     * onSetSpan
     *
     * Creates description tags
     ***/
    private void onSetspan() {
        String content = etTags.getText().toString();
        this.desTags.add(content);
        SpannableString spannable = new SpannableString(content);
        String[] m = content.split(" ");
        //       tagsCnt = m.length;
        int start = 0;
        int end;
        for (String str : m) {
            end = start + str.length();
            spannable.setSpan(new BackgroundColorSpan(Color.rgb(255, 136, 0)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end + 1;
        }
        etTags.setText(spannable);
        etTags.setSelection(spannable.length());
        try {
            tags = getTags();
        } catch (JSONException e) {

        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        String content = etTags.getText().toString();
        if(content.length()>0){
            String last = content.substring(content.length()-1,content.length());
            if(i == KeyEvent.KEYCODE_DEL && !last.equals(" ")){
                String[] m = content.split(" ");
                String lastTag = m[m.length-1];
                content = content.substring(0,content.length()-lastTag.length());
                etTags.setText(content);
                return true;
            }
        }
        return false;
    }


}
