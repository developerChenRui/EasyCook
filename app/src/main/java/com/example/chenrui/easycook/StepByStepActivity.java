package com.example.chenrui.easycook;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class StepByStepActivity extends AppCompatActivity {

    TextView stepCount;
    ImageView stepImage;
    ImageView left;
    ImageView right;
    TextView step_description;
    ImageButton close;

    // record the number of step
    int cur;

    // tools implements
    FloatingActionButton voiceControl;
    FloatingActionButton gestureControl;
    FloatingActionButton setClock;


    // fake image and description
    String[] des = {"the first step is to put the oil in the hot pot","the second step is to put the onion into oil and fryyyyyy" +
            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" +
            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" +
            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy","hhhahahahahaahahah"};
    int[] imageRes = {R.drawable.cut_egg,R.drawable.salad,R.drawable.hamburger};
    int numOfImage = des.length;

    public void performLeft(){
        cur--;
        step_description.setText(des[cur]);
        stepImage.setImageResource(imageRes[cur]);
        stepCount.setText("Step " + (cur+1) + " Of " + des.length);

        // when it comes to the first step or last step , hide left button or right button

        if(cur ==0 && cur==numOfImage-1) {
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
        } else if(cur==numOfImage-1) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
        } else if(cur ==0) {
            left.setVisibility(View.GONE);
            right.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        }
    }

    public void performRight() {
        cur++;
        step_description.setText(des[cur]);
        stepImage.setImageResource(imageRes[cur]);
        stepCount.setText("Step " + (cur+1) + " Of " + des.length);

        // when it comes to the first step or last step , hide left button or right button
        if(cur ==0 && cur==numOfImage-1) {
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
        } else if(cur==numOfImage-1) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
        } else if(cur ==0) {
            left.setVisibility(View.GONE);
            right.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_by_step);

        // get the instruction from previous activity
//        ArrayList<String> instructions = getIntent().getExtras().getStringArrayList("stepBystepInstructions");




        cur = 0;
        stepImage = findViewById(R.id.stepImage);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        step_description = findViewById(R.id.step_description);
        stepCount = findViewById(R.id.stepCount);
        close = findViewById(R.id.btnClose);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        left.setVisibility(View.GONE);

        step_description.setText(des[cur]);
        stepImage.setImageResource(imageRes[cur]);
        stepCount.setText("Step " + (cur+1) + " Of " + des.length);



        // implement left button listener

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLeft();
            }
        });
        // implement right button listener

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRight();
            }
        });

        // tools initialization
        voiceControl = findViewById(R.id.voiceControl);
        gestureControl = findViewById(R.id.gestureControl);
        setClock = findViewById(R.id.setClock);

        // tools listener
        voiceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder customizeDialog =
                        new AlertDialog.Builder(StepByStepActivity.this);
                final View dialogView = LayoutInflater.from(StepByStepActivity.this)
                        .inflate(R.layout.voice_setting_dialog,null);

                TextView title = new TextView(StepByStepActivity.this);
                title.setTextSize(25);
                title.setPadding(0,30,0,30);

                title.setText("Voice Setting");
                title.setGravity(Gravity.CENTER);
                customizeDialog.setCustomTitle(title);

                customizeDialog.setView(dialogView);
                customizeDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO start the voice control here
                                // add the mute permission here
                                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                    getApplicationContext().startActivity(intent);
                                    return;
                                }
                                startService(new Intent(StepByStepActivity.this, VoiceControlService.class));
                            }
                        });
                customizeDialog.show();
            }
        });



    }

}
