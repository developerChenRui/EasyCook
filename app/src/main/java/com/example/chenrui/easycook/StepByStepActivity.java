package com.example.chenrui.easycook;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

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


    // fake image and description
//    String[] des = {"the first step is to put the oil in the hot pot","the second step is to put the onion into oil and fryyyyyy" +
//            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" +
//            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" +
//            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy","hhhahahahahaahahah"};
    int[] imageRes; //= {R.drawable.cut_egg,R.drawable.salad,R.drawable.hamburger};
    int numOfImage;

    public void performLeft(){
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
        stepImage.setImageResource(imageRes[cur]);

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
        stepImage.setImageResource(imageRes[cur]);
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


        // get the instructions
        try {
            des = new JSONArray(getIntent().getStringExtra("stepBystepInstructions"));
            System.out.format("Got step by step instructions %s%n", des.toString());
        } catch (JSONException e) {
            System.err.format("Error getting step descriptions: %s%n",e);
        }
        numOfImage = des.length();
        imageRes = new int[numOfImage];

        for(int i=0; i<numOfImage; i++) {
            imageRes[i] = R.drawable.defaultstep;
        }

        // register the broadcast to receive the message from voice control service
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(RECEIVER_MESSAGE);
                // call any method you want here
                //char[] mesArray = message.toCharArray();
                //HashSet<Character> mesSet = new HashSet<Character>();
//                for(Character c : mesArray) {
//                    mesSet.add(c);
//                }

                if(message.equals(leftCommand) && cur>0) {
                    performLeft();
                }
                if(message.equals(rightCommand) && cur<des.length()) {
                    performRight();
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
        stepImage.setImageResource(imageRes[cur]);
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
                                        return;
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
                    customizeDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO start the voice control here
                                    gestureControlOn = true;

                                    invert = ((CheckBox)dialogView.findViewById(R.id.checkInvert)).isChecked();
                                    if (invert) {
                                        ((TextView)dialogView.findViewById(R.id.txtRight)).setText("Swipe Right: Next");
                                        ((TextView)dialogView.findViewById(R.id.txtLeft)).setText("Swipe Left: Previous");
                                    } else {
                                        ((TextView)dialogView.findViewById(R.id.txtRight)).setText("Swipe Right: Previous");
                                        ((TextView)dialogView.findViewById(R.id.txtLeft)).setText("Swipe Left: Next");
                                    }
                                    // add the mute permission here
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                        StepByStepActivity.this.startActivity(intent);
                                        return;
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
