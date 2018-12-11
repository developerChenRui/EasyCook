package com.example.chenrui.easycook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
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

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CreateActivity extends AppCompatActivity {

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

    public RecipesFragment favoriteFragment;

    List<String> source = new ArrayList<>();
    private static final boolean isChinese2English = false;

    private static final int STEP_PIC = 222;

    public static void setEditTextInhibitInputSpeChat(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String chars = "\r\n\t ";
                String speChat = "[" + chars + "]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if (matcher.find()) {
                    String str = source.toString();
                    char[] charArr = toCharArray(chars);
                    for (char c : charArr) {
                        str = str.replaceAll(new String(new char[]{c}), "");
                    }
                    return str;
                } else return null;
            }
        };
        InputFilter emojiFilter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int index = start; index < end; index++) {
                    int type = Character.getType(source.charAt(index));
                    if (type == Character.SURROGATE) {
                        return "";
                    }
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter, emojiFilter});
    }


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

        findViewById(R.id.des).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSoftInputVis(etTags, true);
            }
        });

        init();
        etTags.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_COMMA) {
                    initTags();
//                    if(!desTags.contains(temp)) {
//                        desTags.add(temp);
//                    }
                    return true;
                }
                return false;
            }
        });

        etTags.addTextChangedListener(new TextWatcher() {
            int start;
            int count;
            int before;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.before = before;
                this.count = count;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (count <= 0) {
                    return;
                }
                etTags.removeTextChangedListener(this);
                if (Selection.getSelectionEnd(editable) != start + count) {
                    editable.replace(start, start + count, "");
                } else {
                    onChange(editable);
                }
                etTags.addTextChangedListener(this);
            }

            private void onChange(Editable editable) {
                String changeString = editable.subSequence(start, start + count).toString();
                int sumOfComma = removeAllComma(editable);
                count -= sumOfComma;
//                count -= delIfOverMax();
                if (sumOfComma > 0) {
                    initTags();
                    return;
                }
                if (count == 0) {
                    return;
                }
                setTextSpan(editable);
            }

            private void setTextSpan(Editable editable) {
                CharSequence string = editable.subSequence(start, start + count);
                char[] chars = toCharArray(string);
                int i = 0;
                for (char c : chars) {
                    editable.setSpan(new MyImageSpanText(CreateActivity.this, getImage(new String(new char[]{c}), false)),
                            start + i, start + i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i++;
                }
            }

            private int removeAllComma(Editable editable) {
                int sum = 0;
                while (editable.toString().contains(",")) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    int i = editable.toString().indexOf(",");
                    editable.replace(i, i + 1, "");
                    Selection.setSelection(editable, selEndIndex - (selEndIndex >= i ? 1 : 0));
                    sum++;
                }
                return sum;
            }
        });

        setEditTextInhibitInputSpeChat(etTags);


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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateActivity.this, NavigateActivity.class);
                i.putExtra("id", 1);
                startActivity(i);
                finish();
            }
        });

        //check if the required fields are missing
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(enterName.getText()) || TextUtils.isEmpty(etTags.getText()) ||
                        TextUtils.isEmpty(time.getText())) {
                    initDialog();
                    if (alertDialog != null && !alertDialog.isShowing()) {
                        alertDialog.show();
                        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
                        alertDialog.getWindow().setAttributes(params);
                    }
                } else {
                    String imageName = "coverImg_"
                            + Utils.user.getEmail().toString().replaceAll("@", "_")
                            .replaceAll(" ", "_")
                            .replaceAll(".", "_")
                            .replaceAll(",","_")
                            + enterName.getText().toString();
//                String imageName = "coverImg_wmx_bu_edu" + enterName.getText().toString();

                    String path = adapter.getPath();
                    Log.i("path", path);
                    Bitmap bitmap = adapter.getBitmap();
                    Log.i("bitmapSave", "" + bitmap.getByteCount());
                    ImageSaver imageSaver = new ImageSaver();
                    imageSaver.pushImage(imageName, bitmap, getBaseContext().getFilesDir(), new ImageCallback() {
                        @Override
                        public void onCallback(String imageURL) {
                            Log.i("imageURL", "success" + imageURL);
                            System.out.print("success");
                            try {

                                String userName = Utils.user.getEmail().toString();
                                String recipeName = enterName.getText().toString();
                                String recipeID = enterName.getText().toString() + "_" + userName.replace('.','_').replace('@','_');
                                System.out.println("recipe " + imageURL.toString() + " "  + recipeID + " " + userName);
                                JSONArray instructions = getInstructions();
                                JSONArray ingredients = getIngredients();
                                System.out.println("instruction: " + instructions.toString());
                                System.out.println("ingredients: " + ingredients.toString());
                                JSONArray tags = getTags(desTags);
                                System.out.println("etTags: " + etTags.getText().toString());
                                System.out.println("tags: " + tags.toString());

//        recipe = new Recipe();

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

//                                createRecipe(imageURL);
                                RecipeSaver recipeSaver = new RecipeSaver();
                                recipeSaver.setRecipe(recipe);
                                recipeSaver.pushRecipe(getBaseContext().getFilesDir());
                                Log.i("PUSH", "success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Intent i = new Intent(CreateActivity.this, NavigateActivity.class);
                    i.putExtra("id", 1);
//                i.putExtra("bitmap", bitmapByte);
                    startActivity(i);

                    //return to MyRecipes
                    Toast.makeText(CreateActivity.this, "Submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
/*
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Should be the implementation of submit!!!!!!!
                               String imageName = "coverImg_"
                                       + Utils.user.getEmail().toString().replaceAll("@", "_")
                                       .replaceAll(" ", "_")
                                       + enterName;
//                String imageName = "coverImg_wmx_bu_edu" + enterName.getText().toString();

                String path = adapter.getPath();
                Log.i("path", path);
                Bitmap bitmap = adapter.getBitmap();
                Log.i("bitmapSave", "" + bitmap.getByteCount());
                ImageSaver imageSaver = new ImageSaver();
                imageSaver.pushImage(imageName, bitmap, getBaseContext().getFilesDir(), new ImageCallback() {
                    @Override
                    public void onCallback(String imageURL) {
                        Log.i("imageURL", "success" + imageURL);
                        System.out.print("success");
                        try {
                            createRecipe(imageURL);
                            RecipeSaver recipeSaver = new RecipeSaver();
                            recipeSaver.setRecipe(recipe);
                            recipeSaver.pushRecipe(getBaseContext().getFilesDir());
                            Log.i("PUSH", "success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Intent i = new Intent(CreateActivity.this, NavigateActivity.class);
                i.putExtra("id", 1);
//                i.putExtra("bitmap", bitmapByte);
                startActivity(i);
                Toast.makeText(CreateActivity.this, "Saved as draft", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
*/

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingAdapter.addData(ingList.size());
            }
        });

        step_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateActivity.this, "add clicked", Toast.LENGTH_SHORT).show();
//                resetMedia();
                insAdapter.addData(insList.size());
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

    private JSONArray getInstructions() {
        JSONArray instructions = new JSONArray();


        int count = insAdapter.getItemCount();

         for(int i = 0; i < count; i++) {
            JSONObject step = new JSONObject();
//            String url = "";
            String imageName = enterName.getText().toString() + "_" + i;
            String path = insAdapter.getPath();
            Log.i("path", path);
            Bitmap bitmap = insAdapter.getBitmap(path);
            Log.i("bitmapSave", "" + bitmap.getByteCount());
            ImageSaver imageSaver = new ImageSaver();
            imageSaver.pushImage(imageName, bitmap, getBaseContext().getFilesDir(), new ImageCallback() {
                @Override
                public void onCallback(String imageURL) {
 //                   Log.i("step", "success" + imageURL);
                    System.out.print("step upload success");
                    try {
                        step.put("image", imageURL);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            try {
                Log.i("step", "success" + insAdapter.getDetail());
                step.put("details", insAdapter.getDetail());
                System.out.print("step " + i + step.toString());
                instructions.put(i, step);
//                System.out.print("step" + i + instructions.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return instructions;
    }



    private JSONArray getIngredients() throws JSONException {
        JSONArray ingredients = new JSONArray();
        int ingredientCnt = ingAdapter.getItemCount();

        for(int i = 0; i < ingredientCnt; i ++) {

            JSONObject content = new JSONObject();

            Float amount = null;
            String unit = "";
            String ingre = "";

            amount = ingAdapter.getAmount();
            unit = ingAdapter.getUnit();
            ingre = ingAdapter.getIngredient();

            content.put("amount", amount);
            content.put("unit", unit);
            content.put("ingredient", ingre);

            ingredients.put(i, content);
        }

        return ingredients;
    }

    private JSONArray getTags(ArrayList<String> desTags) throws JSONException {

            tags.put(0, desTags.get(0));
            for(int i = 1; i < desTags.size(); i++) {
                String prev = desTags.get(i - 1);
                int size = prev.length();

                tags.put(i, desTags.get(i).substring(size));
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
                    Toast.makeText(CreateActivity.this,
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

    @NonNull
    private static MyImageSpanImage[] getSortedImageSpans(final Editable text) {
        MyImageSpanImage[] spans = text.getSpans(0, text.length(), MyImageSpanImage.class);

        Arrays.sort(spans, new Comparator<MyImageSpanImage>() {
            @Override
            public int compare(MyImageSpanImage o1, MyImageSpanImage o2) {
                int start1 = text.getSpanStart(o1);
                int start2 = text.getSpanStart(o2);
                if (start1 > start2) {
                    return 1;
                } else if (start1 < start2) {
                    return -1;
                }
                return 0;
            }
        });
        return spans;
    }
    private int delIfOverMax() {
        final Editable text = etTags.getText();
        int selEndIndex = Selection.getSelectionEnd(text);
        int lastEnd = 0;
        MyImageSpanImage[] spans = getSortedImageSpans(text);
        for (MyImageSpanImage span : spans) {
            int start = text.getSpanStart(span);
            int length = init(text, selEndIndex, lastEnd, start);
            if (length > 0)
                return length;

            int end = text.getSpanEnd(span);
            lastEnd = Math.max(end, lastEnd);
        }
        int length = text.length();
        length = init(text, selEndIndex, lastEnd, length);
        if (length > 0)
            return length;

        return 0;
    }

    private int init(Editable text, int selectedIndex, int start, int end) {
        if (start >= end) {
            return 0;
        }
        if (selectedIndex >= start && selectedIndex <= end) {
            String blockString = text.subSequence(start, end).toString();
            int length = calculateLength(blockString);
            if (length > maxLength) {
                String rightString = text.subSequence(selectedIndex, end).toString();
                String leftString = text.subSequence(start, selectedIndex).toString();
                int rightLength = calculateLength(rightString);
                int leftLength = calculateLength(leftString);
                //
                char[] leftStringChars = toCharArray(leftString);
                int okIndex = selectedIndex;
                int charSum = 0;
                int leaveLength = maxLength - rightLength;
                for (int i = leftStringChars.length - 1; i >= 0; i--) {
                    char c = leftStringChars[i];
                    if ((c & 0xffff) <= 0xff) {
                        charSum += 1;
                        okIndex--;
                    } else {
                        charSum += 2;
                        okIndex--;
                    }
                    int nowLeaveLength = leftLength - charSum;
                    if (nowLeaveLength <= leaveLength) {
                        break;
                    }
                }
                text.replace(okIndex, selectedIndex, "");
                return selectedIndex - okIndex;
            }
        } else {
            setImageSpan(text, start, end);
        }
        return 0;
    }


    private static char[] toCharArray(CharSequence str) {
        if (str instanceof SpannableStringBuilder) {
            str.length();
        }
        char[] charArray = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            charArray[i] = str.charAt(i);
        }
        return charArray;
    }



    private void init() {
        StringBuilder sb = new StringBuilder();
        for (String str : source) {
            str = str.trim().replaceAll(",", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
            if (str.length() == 0) {
                continue;
            }
            sb.append(str);
        }

        etTags.setText(sb.toString());

        etTags.setSelection(etTags.length());

        int length = 0;
        Editable text = etTags.getText();
        for (String str : source) {
            str = str.trim().replaceAll(",", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
            if (str.length() == 0) {
                continue;
            }
            int strLength = str.length();
            Bitmap tagImage = getTagImage(str);
            text.setSpan(new MyImageSpanImage(this, tagImage), length, strLength + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            length += strLength;
        }
    }

    private void processImageSpan() {
        final Editable text = etTags.getText();
        MyImageSpanImage[] spans = getSortedImageSpans(text);
        source.clear();
        int lastEnd = 0;
        for (MyImageSpanImage span : spans) {
            int start = text.getSpanStart(span);
            int end = text.getSpanEnd(span);
            if (lastEnd == end || start == end) {
                lastEnd = end;
                continue;
            }
            lastEnd = end;
            source.add(text.toString().substring(start, end));
        }

        this.source = source;
        finish();
    }

    public static float getTextViewLength(TextView textView, String text) {
        if (text == null) {
            return 0;
        }
        TextPaint paint = textView.getPaint();
        float textLength = paint.measureText(text);
        return textLength;
    }

    private static class MyImageSpanText extends ImageSpan {

        public MyImageSpanText(Context context, Bitmap b) {
            super(context, b);
        }
    }

    private static class MyImageSpanImage extends ImageSpan {

        public MyImageSpanImage(Context context, Bitmap b) {
            super(context, b);
        }
    }

    public void setSoftInputVis(View view, boolean vis) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (vis) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public int getPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    public int calculateLength(CharSequence c) {

        if (!isChinese2English) {
            return c.length();
        }

        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            char cc = c.charAt(i);
            if ((cc & 0xffff) <= 0xff) {
                len += 0.5;
            } else {
                len++;
            }
        }
        len = len * 2;
        return (int) Math.round(len);
    }
    private void initTags() {
        final Editable text = etTags.getText();
        if(!desTags.contains(text.toString())) {
            desTags.add(text.toString());
        }
        int lastEnd = 0;
        MyImageSpanImage[] spans = getSortedImageSpans(text);

        for (MyImageSpanImage span : spans) {
            int start = text.getSpanStart(span);
            int end = text.getSpanEnd(span);
            if (lastEnd < start) {
                setImageSpan(text, lastEnd, start);
            }

            lastEnd = Math.max(end, lastEnd);
        }
        if (lastEnd != text.length()) {
            setImageSpan(text, lastEnd, text.length());
        }

    }


    private void setImageSpan(Editable text, int start, int end) {
        Bitmap tagImage = getTagImage(text.subSequence(start, end).toString());
        for (MyImageSpanText span2 : text.getSpans(0, etTags.length(), MyImageSpanText.class)) {
            text.removeSpan(span2);
        }
        text.setSpan(new MyImageSpanImage(this, tagImage), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private Bitmap getImage(String string, boolean isHint) {
        if (string == null) {
            return null;
        }
        FrameLayout fl = new FrameLayout(this);
        fl.setPadding(0, getPx(6), 0, getPx(6));
        TextView tv = new TextView(this);
        tv.setMaxLines(1);
        tv.setLines(1);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setText(string);
        tv.setTextColor(isHint ? 0xff888888 : 0xff444444);
        fl.addView(tv);
        return getBitmapViewByMeasure(fl, (int) getTextViewLength(tv, string), getPx(32));
    }


    private Bitmap getTagImage(String string) {
        if (string == null) {
            return null;
        }
        string = string.replaceAll(",", "");
        if (string.length() == 0) {
            return null;
        }
        FrameLayout fl = new FrameLayout(this);
        fl.setPadding(getPx(4), getPx(2), getPx(4), getPx(2));
        TextView tv = new TextView(this);
        tv.setPadding(getPx(4), getPx(4), getPx(4), getPx(4));
        tv.setMaxLines(1);
        tv.setLines(1);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setText(string);
        tv.setTextColor(0xffffffff);
        tv.setBackgroundResource(R.drawable.tag_bg);
        fl.addView(tv);
        return getBitmapViewByMeasure(fl, (int) getTextViewLength(tv, string) + getPx(16), getPx(32));
    }

    public static Bitmap getBitmapViewByMeasure(View view, int width, int height) {

        view.setDrawingCacheEnabled(true);

        if (height <= 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        } else if (height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = null;
        try {
            bitmap = view.getDrawingCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
