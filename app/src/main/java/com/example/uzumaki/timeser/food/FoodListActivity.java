package com.example.uzumaki.timeser.food;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uzumaki.timeser.Util.AppUtils;
import com.example.uzumaki.timeser.model.Food;

import java.util.ArrayList;
import java.util.List;

import ln.designdemo.R;


public class FoodListActivity extends AppCompatActivity {

    public static final String SELECTED_FOOD_TYPE = "selected_foodtype";
    public static final String SELECTED_FOOD_TIMING = "selected_foodrating";

    private FoodListAdapter adapter;
    private RecyclerView recyclerView;
    private View foodListView;
    private Toolbar toolbar;
    private TextView errorMessageView;
    private List<Food> foodList = new ArrayList<Food>();

    private TextView tView;
    private Button btnStart,btnPause,btnResume,btnCancel;
    CountDownTimer waitTimer;

    private boolean isPaused = false;
    //Declare a variable to hold count down timer's paused status
    private boolean isCanceled = false;

    //Declare a variable to hold CountDownTimer remaining time
    private long timeRemaining = 0;

    int duration = 0; //mins


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        getData();
        bindViews();
    }

    private void getData() {
        if(AppUtils.responseMap.get(AppUtils.FOOD_RESPONSE) != null) {
            foodList = (List<Food>) AppUtils.responseMap.get(AppUtils.FOOD_RESPONSE);
        }
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        errorMessageView = findViewById(R.id.error);
        recyclerView = findViewById(R.id.food_list);
        foodListView = findViewById(R.id.food_list_view);
        tView = findViewById(R.id.tv_timer);
        btnStart = findViewById(R.id.start_button);
        btnPause = findViewById(R.id.pause_button);
        btnResume = findViewById(R.id.resume_button);
        btnCancel = findViewById(R.id.reset_button);

        adapter = new FoodListAdapter(foodList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.food_details);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getFilters();
        timer();
    }

    private void getFilters() {
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null) {
            String selectedfoodType = intent.getExtras().getString(SELECTED_FOOD_TYPE);
            String selectedfoodTiming= intent.getExtras().getString(SELECTED_FOOD_TIMING);
            parseResults(selectedfoodType,selectedfoodTiming);
        }
    }

    @Override
    public void onBackPressed() {
    finish();

    }
    private void parseResults(String selectedFoodType, String selectedFoodTiming) {
        this.duration = Integer.parseInt(selectedFoodTiming);
        List<Food> filteredList = new ArrayList<>();
        for(Food food : foodList) {
            if(food.getName().equalsIgnoreCase(selectedFoodType) && String.valueOf(food.getTiming()).equalsIgnoreCase(selectedFoodTiming)) {
                filteredList.add(food);
            }
        }

        if(filteredList.size() > 0) {
            Toast.makeText(getApplicationContext(),"Found "+filteredList.size() +" match",Toast.LENGTH_LONG).show();
            adapter.setItems(filteredList);
            adapter.notifyDataSetChanged();
            errorMessageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            foodListView.setVisibility(View.VISIBLE);
        } else {
            errorMessageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            foodListView.setVisibility(View.GONE);
        }
    }


    private void timer() {
        //Get reference of the XML layout's widgets

        //Initially disabled the pause, resume and cancel button
        btnPause.setEnabled(false);
        btnResume.setEnabled(false);
        btnCancel.setEnabled(false);
        tView.setText(convertSecondsToTime(duration * 60));

        //Set a Click Listener for start button
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startSound();
                isPaused = false;
                isCanceled = false;

                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enabled the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);

                CountDownTimer timer;
                long millisInFuture = 1000 * 60 * duration; //30 seconds
                long countDownInterval = 1000; //1 second


                //Initialize a new CountDownTimer instance
                new CountDownTimer(millisInFuture, countDownInterval) {
                    public void onTick(long millisUntilFinished) {
                        //do something in every tick
                        if (isPaused || isCanceled) {
                            //If the user request to cancel or paused the
                            //CountDownTimer we will cancel the current instance
                            cancel();
                        } else {
                            //Display the remaining seconds to app interface
                            //1 second = 1000 milliseconds
                            tView.setText("" + convertSecondsToTime(millisUntilFinished / 1000));
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                        }
                    }

                    public void onFinish() {
                        //Do something when count down finished
                        tView.setText("Done");

                        //Enable the start button
                        btnStart.setEnabled(true);
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        stopSound();
                    }
                }.start();
            }
        });

        //Set a Click Listener for pause button
        btnPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //When user request to pause the CountDownTimer
                isPaused = true;

                //Enable the resume and cancel button
                btnResume.setEnabled(true);
                btnCancel.setEnabled(true);
                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
            }
        });

        //Set a Click Listener for resume button
        btnResume.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Disable the start and resume button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enable the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);

                //Specify the current state is not paused and canceled.
                isPaused = false;
                isCanceled = false;

                //Initialize a new CountDownTimer instance
                long millisInFuture = timeRemaining;
                long countDownInterval = 1000;
                new CountDownTimer(millisInFuture, countDownInterval){
                    public void onTick(long millisUntilFinished){
                        //Do something in every tick
                        if(isPaused || isCanceled)
                        {
                            //If user requested to pause or cancel the count down timer
                            cancel();
                        }
                        else {
                            tView.setText("" + convertSecondsToTime(millisUntilFinished / 1000));
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                        }
                    }
                    public void onFinish(){
                        //Do something when count down finished
                        tView.setText("Done");
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        //Enable the start button
                        btnStart.setEnabled(true);
                        stopSound();
                    }
                }.start();

                //Set a Click Listener for cancel/stop button
                btnCancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //When user request to cancel the CountDownTimer
                        isCanceled = true;

                        //Disable the cancel, pause and resume button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        //Enable the start button
                        btnStart.setEnabled(true);

                        //Notify the user that CountDownTimer is canceled/stopped
                        tView.setText("Stopped");
                    }
                });
            }
        });

        //Set a Click Listener for cancel/stop button
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //When user request to cancel the CountDownTimer
                isCanceled = true;

                //Disable the cancel, pause and resume button
                btnPause.setEnabled(false);
                btnResume.setEnabled(false);
                btnCancel.setEnabled(false);
                //Enable the start button
                btnStart.setEnabled(true);

                //Notify the user that CountDownTimer is canceled/stopped
                tView.setText("Stopped");
            }
        });
    }

    private String convertSecondsToTime(long totalSecs) {
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        return  String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void startSound() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP,150);
    }

    private void stopSound() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SS,150);
    }
//    public void startTimer(View view) {
//        waitTimer = new CountDownTimer(60000, 300) {
//
//            public void onTick(long millisUntilFinished) {
//                //called every 300 milliseconds, which could be used to
//                //send messages or some other action
//            }
//
//            public void onFinish() {
//                //After 60000 milliseconds (60 sec) finish current
//                //if you would like to execute something when time finishes
//            }
//        }.start();
//    }
//
//    public void pauseTimer(View view) {
//
//    }
//
//    public void resetTimer(View view) {
//        if(waitTimer != null) {
//            waitTimer.cancel();
//            waitTimer = null;
//        }
//    }
}
