package com.example.chenrui.easycook;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {

    private TextView  hour,minute,second;
    private  long  time;
    private ImageView startTimer, restartTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        hour=(TextView)findViewById(R.id.hour);
        minute=(TextView) findViewById(R.id.minute);
        second=(TextView) findViewById(R.id.second);
        startTimer = (ImageView) findViewById(R.id.startTimer);
        restartTimer = (ImageView) findViewById(R.id.restartTimer);


        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = format2second(Integer.parseInt(hour.getText().toString()),
                        Integer.parseInt(minute.getText().toString()),
                        Integer.parseInt(second.getText().toString()));
                handler.postDelayed(runnable, 100);

            }
        });


    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            String formatLongToTimeStr = formatLongToTimeStr(time);
            String[] split = formatLongToTimeStr.split("：");
            for (int i = 0; i < split.length; i++) {
                if(i==0){
                    hour.setText(split[0]);
                }
                if(i==1){
                    minute.setText(split[1]);
                }
                if(i==2){
                    second.setText(split[2]);
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



