package com.example.chenrui.easycook;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import edu.washington.cs.touchfreelibrary.sensors.ClickSensor;
import edu.washington.cs.touchfreelibrary.utilities.LocalOpenCV;
import edu.washington.cs.touchfreelibrary.utilities.PermissionUtility;


public class StepByStepActivity extends AppCompatActivity implements CameraGestureSensor.Listener, ClickSensor.Listener{

    // register a broadcast to make service call the method from this activity
    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";
    public static final String RECEIVER_MESSAGE = "RECEIVER_MESSAGE";
    BroadcastReceiver mBroadcastReceiver;

    private boolean voiceControlOn = false;
    private boolean gestureControlOn = false;


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

    JSONArray des = new JSONArray();

    // command
    String leftCommand;
    String rightCommand;

    // invert for hand gestures
    Boolean invert = false;


    ArrayList<String> imageURLs; //= {R.drawable.cut_egg,R.drawable.salad,R.drawable.hamburger};
    int[] imageRes;
    boolean isSpoon;
    int numOfImage;

    public void performLeft(){
        Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show();
        Animation fadeIn = new AlphaAnimation(0, 1);
        if (cur == 0) {
            return;
        }
        cur--;
        try{
            step_description.setText(des.getJSONObject(cur).getString("step").toString());
        } catch (JSONException e) {

        }
        step_description.startAnimation(fadeIn);
        step_description.setMovementMethod(new ScrollingMovementMethod());

        if(isSpoon) {
            stepImage.setImageResource(imageRes[cur]);
        } else {
            //TODO picasso
            Picasso.get().load(imageURLs.get(cur)).into(stepImage);
        }

        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(1500);
        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.setRepeatCount(1);
        stepImage.setAnimation(animation);
        stepCount.setText("Step " + (cur+1) + " Of " + des.length());


        // when it comes to the first step or last step , hide left button or right button

        if(cur ==0 && cur==numOfImage-1) {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
        } else if(cur==numOfImage-1) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.INVISIBLE);
        } else if(cur ==0) {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        }
    }

    public void performRight() {
        Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
        if (cur == des.length()-1){
            return;
        }
        cur++;
        try{
            step_description.setText(des.getJSONObject(cur).getString("step").toString());
        } catch (JSONException e) {

        }
        Animation fadeIn = new AlphaAnimation(0, 1);
        step_description.setMovementMethod(new ScrollingMovementMethod());
        step_description.startAnimation(fadeIn);
        if(isSpoon) {
            stepImage.setImageResource(imageRes[cur]);
        } else {
            //TODO picasso
            Picasso.get().load(imageURLs.get(cur)).into(stepImage);
        }
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(1500);
        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.setRepeatCount(1);
        stepImage.setAnimation(animation);
        stepCount.setText("Step " + (cur+1) + " Of " + des.length());

        // when it comes to the first step or last step , hide left button or right button
        if(cur ==0 && cur==numOfImage-1) {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
        } else if(cur==numOfImage-1) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.INVISIBLE);
        } else if(cur ==0) {
            left.setVisibility(View.INVISIBLE);
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

        leftCommand = "left";
        rightCommand = "right";

        // get self made recipe images
        Bundle bundle = getIntent().getExtras();
        imageURLs = bundle.getStringArrayList("stepImageArray");


        // get the instructions
        try {
            des = new JSONArray(bundle.getString("stepBystepInstructions"));
            System.out.format("Got step by step instructions %s%n", des.toString());
        } catch (JSONException e) {
            System.err.format("Error getting step descriptions: %s%n",e);
        }
        numOfImage = des.length();

        if(imageURLs.size() <=0) {
            isSpoon = true;
            imageRes = new int[des.length()];
            for(int i=0; i<des.length(); i++) {
                imageRes[i] = R.drawable.defaultstep;
            }
        } else {
            isSpoon = false;
        }
        // register the broadcast to receive the message from voice control service
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(RECEIVER_MESSAGE);

                if(voiceControlOn) {

                    if (message.equals(leftCommand) && cur > 0) {
                        performLeft();
                    }
                    if (message.equals(rightCommand) && cur < des.length()) {
                        performRight();
                    }
                }
            }
        };

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

        try{
            step_description.setText(des.getJSONObject(cur).getString("step").toString());
        } catch (JSONException e) {
            step_description.setText("Failure");
        }

        if(isSpoon) {
            stepImage.setImageResource(imageRes[cur]);
        } else {
            //TODO picasso
            Picasso.get().load(imageURLs.get(cur)).into(stepImage);
        }

        stepCount.setText("Step " + (cur+1) + " Of " + des.length());



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

        // set timer
        setClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder customizeDialog =
                        new AlertDialog.Builder(StepByStepActivity.this);
                final View dialogView = LayoutInflater.from(StepByStepActivity.this)
                        .inflate(R.layout.timer_setting_dialog, null);

                TextView title = new TextView(StepByStepActivity.this);
                title.setTextSize(25);
                title.setPadding(0, 30, 0, 30);

                title.setText("Timer Setting");
                title.setGravity(Gravity.CENTER);
                customizeDialog.setCustomTitle(title);

                customizeDialog.setView(dialogView);
                customizeDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO start the voice control here

                                try {
                                    if (Settings.Global.getInt(getContentResolver(), "zen_mode") != 0) {

                                        AlertDialog.Builder customizeDialog =
                                                new AlertDialog.Builder(StepByStepActivity.this);
                                        customizeDialog.setTitle("Please Turn Off Do-Not-Disturb Mode!");
                                        customizeDialog.setPositiveButton("I Know",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                });
                                        customizeDialog.show();
                                    } else {
                                        Intent i = new Intent(StepByStepActivity.this, TimerActivity.class);
                                        i.putExtra("hour", ((TextView) dialogView.findViewById(R.id.setHour)).getText().toString());
                                        i.putExtra("minute", ((TextView) dialogView.findViewById(R.id.setMinute)).getText().toString());
                                        i.putExtra("second", ((TextView) dialogView.findViewById(R.id.setSecond)).getText().toString());
                                        startActivity(i);
                                    }
                                } catch(Exception e) {

                                }
                            }
                        });
                customizeDialog.show();

            }
        });






        // tools listener
        voiceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (voiceControlOn) {
                    voiceControlOn = false;
                    voiceControl.setFabIcon(getResources().getDrawable(R.drawable.voice_control));
                    stopService(new Intent(StepByStepActivity.this, VoiceControlService.class));
                } else {
                    AlertDialog.Builder customizeDialog =
                            new AlertDialog.Builder(StepByStepActivity.this);
                    final View dialogView = LayoutInflater.from(StepByStepActivity.this)
                            .inflate(R.layout.voice_setting_dialog, null);

                    TextView title = new TextView(StepByStepActivity.this);
                    title.setTextSize(25);
                    title.setPadding(0, 30, 0, 30);

                    title.setText("Voice Setting");
                    title.setGravity(Gravity.CENTER);
                    customizeDialog.setCustomTitle(title);

                    customizeDialog.setView(dialogView);
                    customizeDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO start the voice control here
                                    voiceControlOn = true;
                                    leftCommand = ((TextView)dialogView.findViewById(R.id.lastCommand)).getText().toString();
                                    rightCommand = ((TextView)dialogView.findViewById(R.id.nextCommand)).getText().toString();
                                    leftCommand = leftCommand.isEmpty()?"left":leftCommand;
                                    rightCommand = rightCommand.isEmpty()?"right":rightCommand;
                                    // add the mute permission here
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                        StepByStepActivity.this.startActivity(intent);
                                    }
                                    startService(new Intent(StepByStepActivity.this, VoiceControlService.class));
                                    Toast.makeText(getApplicationContext(), "Start Voice Control", Toast.LENGTH_SHORT).show();
                                    FloatingActionButton voiceControl = findViewById(R.id.voiceControl);
                                    voiceControl.setFabIcon(getResources().getDrawable(R.drawable.quiet));
                                }
                            });
                    customizeDialog.show();
                }
            }
        });

        gestureControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gestureControlOn) {
                    gestureControlOn = false;
                    Toast.makeText(StepByStepActivity.this,"Hand gestures disabled",Toast.LENGTH_SHORT).show();

                } else {
                    AlertDialog.Builder customizeDialog = new AlertDialog.Builder(StepByStepActivity.this);
                    final View dialogView = LayoutInflater.from(StepByStepActivity.this)
                            .inflate(R.layout.gesture_setting_dialog, null);
                    TextView title = new TextView(StepByStepActivity.this);
                    title.setTextSize(25);
                    title.setPadding(0, 30, 0, 30);

                    title.setText("Hand Gesture Setting");
                    title.setGravity(Gravity.CENTER);
                    customizeDialog.setCustomTitle(title);

                    customizeDialog.setView(dialogView);
                    CheckBox inverter = (CheckBox)dialogView.findViewById(R.id.checkInvert);
                    System.out.format("Got inverter %s%n",inverter);
                    inverter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            System.out.format("Invert checked: %s%n", isChecked);
                            invert = isChecked;
                            if (isChecked) {
                                ((TextView)dialogView.findViewById(R.id.txtRight)).setText(R.string.swipe_right_prev);
                                ((TextView)dialogView.findViewById(R.id.txtLeft)).setText(R.string.swipe_left_next);
                            } else {
                                ((TextView)dialogView.findViewById(R.id.txtRight)).setText(R.string.swipe_right_next);
                                ((TextView)dialogView.findViewById(R.id.txtLeft)).setText(R.string.swipe_left_prev);
                            }
                        }
                    });
                    customizeDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO start the voice control here
                                    gestureControlOn = true;



                                    // add the mute permission here
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                        StepByStepActivity.this.startActivity(intent);
                                    }
                                    if (PermissionUtility.checkCameraPermission(StepByStepActivity.this)) {
                                        //The third passing in represents a separate click sensor which is not required if you just want the hand motions
                                        LocalOpenCV loader = new LocalOpenCV(StepByStepActivity.this, StepByStepActivity.this, StepByStepActivity.this);
                                    }
                                    Toast.makeText(getApplicationContext(), "Start Hand Gesture Control", Toast.LENGTH_SHORT).show();
//                                    FloatingActionButton voiceControl = findViewById(R.id.voiceControl);
//                                    voiceControl.setFabIcon(getResources().getDrawable(R.drawable.quiet));
                                }
                            });
                    customizeDialog.show();


                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver),
                new IntentFilter(RECEIVER_INTENT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void onGestureUp(CameraGestureSensor caller, long gestureLength) {
        if(gestureControlOn) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (invert) {
                        performRight();
                    } else {
                        performLeft();
                    }
                    Toast.makeText(StepByStepActivity.this, "Up", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            caller.stop();
        }
    }

    @Override
    public void onGestureDown(CameraGestureSensor caller, long gestureLength) {
        if(gestureControlOn){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (invert) {
                        performLeft();
                    } else {
                        performRight();
                    }
                        Toast.makeText(StepByStepActivity.this,"Down",Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            caller.stop();
        }
    }

    @Override
    public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
        if(gestureControlOn){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (invert) {
                        performRight();
                    } else {
                        performLeft();
                    }
                        Toast.makeText(StepByStepActivity.this,"Left",Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            caller.stop();
        }

    }

    @Override
    public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
        if(gestureControlOn) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (invert) {
                        performLeft();
                    } else {
                        performRight();
                    }
                        Toast.makeText(StepByStepActivity.this,"Right",Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            caller.stop();
        }

    }

    @Override
    public void onSensorClick(ClickSensor caller) {

    }
}
