package com.example.chenrui.easycook.CreateRecipes;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chenrui.easycook.CreateRecipes.adapter.GridImageAdapter;
import com.example.chenrui.easycook.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CreateActivity extends AppCompatActivity {

    private List<LocalMedia> selectList = new ArrayList<>();
    private List<LocalMedia> mediaList = new ArrayList<>();
    private GridImageAdapter adapter;
    private static RecyclerView ingRV;
    private static RecyclerView insRV;
    private static IngRecycleAdapter ingAdapter;
    private static InsRecycleAdapter insAdapter;
    private static List<Integer> ingList = new ArrayList<Integer>();
    private static List<Integer> insList = new ArrayList<Integer>();
    private static ImageView iv_add;
    private static ImageView step_add;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private static int themeId = R.style.picture_white_style;
    private int chooseMode = PictureMimeType.ofAll();

    private static EditText enterName;
    private static EditText enterDes;
    private static EditText time;

    private static final int STEP_PIC = 222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_custom);
        }

        enterName = (EditText) findViewById(R.id.enterName);
        enterDes = (EditText) findViewById(R.id.enterDescription);
        time = (EditText) findViewById(R.id.time);

        ImageView cancel = (ImageView)findViewById(R.id.cancel);
        ImageView submit = (ImageView)findViewById(R.id.up);
        ImageView check = (ImageView)findViewById(R.id.check);
//        ImageButton step_add = (ImageButton)findViewById(R.id.addStep);
        //upload cover images

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
                Toast.makeText(CreateActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CreateActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                }

            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateActivity.this, "Saved as draft", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateActivity.this, "add clicked", Toast.LENGTH_SHORT).show();
                resetMedia();
                insAdapter.addData(insList.size());
            }
        });
    }

    private void resetMedia() {
        mediaList = new ArrayList<>();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        View view = View.inflate(CreateActivity.this, R.layout.warning_dialog, null);
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
                Toast.makeText(CreateActivity.this, "save as draft", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog=builder.create();

    }

    private void initIngRecycle() {
        LinearLayoutManager ingLinearLayoutManager = new LinearLayoutManager(this);
        ingRV.setLayoutManager(ingLinearLayoutManager);
        ingList = ingInitData();
        ingAdapter = new IngRecycleAdapter(CreateActivity.this, ingList);
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

        insAdapter = new InsRecycleAdapter(CreateActivity.this, insList, addPicClickListener);
        insAdapter.setList(mediaList);
        insAdapter.setSelectMax(1);
        insRV.setAdapter(insAdapter);

        insRV.setItemAnimator(new DefaultItemAnimator());
    }

    InsRecycleAdapter.onAddPicClickListener addPicClickListener = new InsRecycleAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            gallery(STEP_PIC);
        }
    };

    private void initInsView() {
        step_add = (ImageButton)findViewById(R.id.addStep);
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
                    }
                    insAdapter.setList(mediaList);
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
            gallery(PictureConfig.CHOOSE_REQUEST);
        }
    };

    private void gallery(int requestCode) {
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
                .selectionMedia(selectList)
                .minimumCompressSize(100)
                .forResult(requestCode);
    }

}
