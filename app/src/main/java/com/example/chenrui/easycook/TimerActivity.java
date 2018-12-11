package com.example.chenrui.easycook;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;

public class TimerActivity extends AppCompatActivity {

    private TextView  hour,minute,second;
    private  long  time;
    private FloatingActionButton startTimer, restartTimer;

    private ImageView slideDown;

    int state = 1;

    int setHour;
    int setMinute;
    int setSecond;

    Vibrator vibrator;
    MediaPlayer mp;

    TimerActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        hour=(TextView)findViewById(R.id.hour);
        minute=(TextView) findViewById(R.id.minute);
        second=(TextView) findViewById(R.id.second);
        startTimer =  findViewById(R.id.startTimer);
        restartTimer =  findViewById(R.id.restartTimer);
        slideDown = findViewById(R.id.slidedown);

        slideDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent i = getIntent();
        setHour = i.getStringExtra("hour").isEmpty()?0:Integer.parseInt(i.getStringExtra("hour"));
        setMinute = i.getStringExtra("minute").isEmpty()?5:Integer.parseInt(i.getStringExtra("minute"));
        setSecond = i.getStringExtra("second").isEmpty()?0:Integer.parseInt(i.getStringExtra("second"));

        if(setHour < 10) {
            hour.setText("0"+setHour);
        } else {
            hour.setText(""+setHour);
        }

        if(setMinute < 10) {
            minute.setText("0"+setMinute);
        } else {
            minute.setText(""+minute);
        }

        if(setSecond < 10) {
            second.setText("0"+setSecond);
        } else {
            second.setText(setSecond + "");
        }

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = format2second(Integer.parseInt(hour.getText().toString()),
                        Integer.parseInt(minute.getText().toString()),
                        Integer.parseInt(second.getText().toString()));


                if(state == 1) {
                    handler.postDelayed(runnable, 100);
                    state = 0;
                    startTimer.setFabIcon(getResources().getDrawable(R.drawable.stop));
                } else {
                    handler.removeCallbacks(runnable);
                    state = 1;
                    startTimer.setFabIcon(getResources().getDrawable(R.drawable.play));
                }

            }
        });

        restartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setHour < 10) {
                    hour.setText("0"+setHour);
                } else {
                    hour.setText(""+setHour);
                }

                if(setMinute < 10) {
                    minute.setText("0"+setMinute);
                } else {
                    minute.setText(""+minute);
                }

                if(setSecond < 10) {
                    second.setText("0"+setSecond);
                } else {
                    second.setText(setSecond + "");
                }
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        if(vibrator != null) {
            vibrator.cancel();
        }

        if(mp !=null) {
            mp.release();
        }
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            if(time == 10 ) {
                mp = new MediaPlayer();
                try {
                mp.setDataSource(getBaseContext(), RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                    mp.prepare();
                } catch (Exception e) {

                }
                mp.start();
                mp.setLooping(true); //循环播放

                vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{1000, 2500, 1000, 2500}, -1);

            }
            if(time <=0) {
                vibrator.cancel();
                mp.release();
                finish();
                TimerActivity.this.finish();
            }

            String formatLongToTimeStr = formatLongToTimeStr(time);
            String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if(i==0){
                    if(Integer.parseInt(split[0]) < 10) {
                        hour.setText("0"+split[0]);
                    } else {
                        hour.setText(split[0]);
                    }
                }
                if(i==1){
                    if(Integer.parseInt(split[1]) < 10) {
                        minute.setText("0"+split[1]);
                    } else {
                        minute.setText(split[1]);
                    }
                }
                if(i==2){
                    if(Integer.parseInt(split[2]) < 10) {
                        second.setText("0"+split[2]);
                    } else {
                        second.setText(split[2]);
                    }
                }

            }
            if(time>0){
                handler.postDelayed(this, 1000);
            }
        }
    };



    public  String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue() ;
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour+"："+minute+"："+second;
        return strtime;

    }

    public long format2second(int hour, int minute, int second) {
        return hour * 3600 + minute * 60 + second;
    }
}



