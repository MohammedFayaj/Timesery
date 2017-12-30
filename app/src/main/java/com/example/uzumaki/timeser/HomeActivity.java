package com.example.uzumaki.timeser;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ln.designdemo.R;

public class HomeActivity extends AppCompatActivity {
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private TextView timerValue;
    private Handler customHandler = new Handler();
    private long startTime = 0;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;

    long updatedTime = 0L;

    public boolean go = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        timerValue = (TextView) findViewById(R.id.timerValue);
        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startTime = SystemClock.uptimeMillis();
                timeSwapBuff = 0L;
                customHandler.postDelayed(updateTimerThread,0);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }
        });
    }
    private Runnable updateTimerThread= new Runnable(){
        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff +timeInMilliseconds;
            int secs = (int) (updatedTime/1000);
            int mins = secs / 60;
            secs= secs%60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":" + String.format("%02d", secs)+":"+ String.format("%02d",milliseconds));
            customHandler.postDelayed(this,0);
        }
    };
}
