package com.example.chenrui.easycook;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateRecipeActivity extends AppCompatActivity {

    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);

    private static RecyclerView ingRV;
    private static RecyclerView insRV;
    private static IngRecycleAdapter ingAdapter;
    private static InsRecycleAdapter insAdapter;
    private static List<Integer> ingList = new ArrayList<Integer>();
    private static List<Integer> insList = new ArrayList<Integer>();
    private static ImageView iv_add;
    private static ImageView step_add;
    private AlertDialog alertDialog;


    private static EditText enterName;
    private static EditText enterDes;
    private static EditText time;
    private static TextView recipeName;
    private static TextView description;
    private static TextView ing;
    private ImageView pic;

    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_custom);
        }

        enterName = (EditText) findViewById(R.id.enterName);
        enterDes = (EditText) findViewById(R.id.enterDescription);
        time = (EditText) findViewById(R.id.time);
        pic = (ImageButton)findViewById(R.id.pic);

        ImageView cancel = (ImageView)findViewById(R.id.cancel);
        ImageView submit = (ImageView)findViewById(R.id.up);
        ImageView check = (ImageView)findViewById(R.id.check);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Toast.makeText(CreateRecipeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();

            }
        });

        //check if the required fields are missing
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(enterName.getText()) || TextUtils.isEmpty(enterDes.getText()) ||
                        TextUtils.isEmpty(time.getText())) {
                    initDialog();
                    if (alertDialog!=null&&!alertDialog.isShowing()) {
                        alertDialog.show();
                        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
                        alertDialog.getWindow().setAttributes(params);
                    }
                }else {
//                    System.out.println(enterName.toString());
                    Toast.makeText(CreateRecipeActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                }

            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateRecipeActivity.this, "Saved as draft", Toast.LENGTH_SHORT).show();
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            int index = 0;
            @Override
            public void onClick(View view) {
                onDialog(view);
            }
        });

        initInsView();
        initInsRecycle();

        initIngView();
        initIngRecycle();

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingAdapter.addData(ingList.size());
            }
        });

        step_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insAdapter.addData(insList.size());
            }
        });
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipeActivity.this);
        View view = View.inflate(CreateRecipeActivity.this, R.layout.warning_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);

        Button back = (Button)view.findViewById(R.id.back);
        Button draft = (Button)view.findViewById(R.id.draft);
        Button cancel = (Button)view.findViewById(R.id.exit);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateRecipeActivity.this, "save as draft", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateRecipeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog=builder.create();

    }

    private void initIngRecycle() {
        LinearLayoutManager ingLinearLayoutManager = new LinearLayoutManager(this);
        ingRV.setLayoutManager(ingLinearLayoutManager);
        ingList = ingInitData();
        ingAdapter = new IngRecycleAdapter(CreateRecipeActivity.this, ingList);
        ingRV.setAdapter(ingAdapter);
        ingRV.setItemAnimator(new DefaultItemAnimator());

    }

    private void initIngView() {
        iv_add = (ImageView) findViewById(R.id.iv_add);
        ingRV = (RecyclerView) findViewById(R.id.ingredients);
    }

    private void initInsRecycle() {
        LinearLayoutManager insLinearLayoutManager = new LinearLayoutManager(this);
        insRV.setLayoutManager(insLinearLayoutManager);
        insList = insInitData();
        insAdapter = new InsRecycleAdapter(CreateRecipeActivity.this, insList);
        insAdapter.onBind = (viewHolder, position) -> {
            viewHolder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDialog(view);
                }
            });

        };
        insRV.setAdapter(insAdapter);
        insRV.setItemAnimator(new DefaultItemAnimator());

    }

    private void camera(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),PHOTO_FILE_NAME);
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
        }
    }

    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "png");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void gallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    private void initInsView() {
        step_add = (ImageView)findViewById(R.id.addStep);
        insRV = (RecyclerView)findViewById(R.id.steps);
    }

    protected ArrayList<Integer> ingInitData() {
        ArrayList<Integer> mDatas = new ArrayList<Integer>();
        for (int i = 0; i < 1; i++) {
            mDatas.add(R.layout.item_layout);
        }
        return mDatas;
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
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(CreateRecipeActivity.this, "Can't find sd card!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                pic.setImageBitmap(bitmap);
            }
            try {
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onDialog(View view)
    {
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(null);
        dialog.show();
//      dialog.getWindow().setWindowAnimations();
        slideToUp(dialog.getWindow().findViewById(R.id.layout));

    }

    public void slideToUp(View view){
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(200);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Button album = (Button)view.findViewById(R.id.album);
                Button camera = (Button)view.findViewById(R.id.camera);

                album.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gallery(view);
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        camera(view);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
