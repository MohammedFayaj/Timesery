package com.example.uzumaki.timeser.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.uzumaki.timeser.food.FoodListActivity;
import com.example.uzumaki.timeser.model.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ln.designdemo.R;

public class HomeActivity extends AppCompatActivity implements HomeContract.View{

    private AppCompatSpinner foodType,foodTiming;
    private Button searchBtn;
    private ProgressDialog pDialog;
    HomeContract.Presenter presenter;
    private List<Food> foodList = new ArrayList<Food>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        presenter = new HomePresenter(this);
        bindViews();
        loadData();
    }

    private void bindViews() {
        foodType = (AppCompatSpinner) findViewById(R.id.food_type);
        foodTiming = (AppCompatSpinner) findViewById(R.id.food_timing);
        searchBtn = (Button) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFood();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                HomeActivity.this);

        // set title
        alertDialogBuilder.setTitle(R.string.alert_title);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.alert_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.positive_btn,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        HomeActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.negative_btn,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();		}



    private void loadData() {
        presenter.onCreate();
    }


    /**
     * Set up the spinner based on the items fetched from the response
     */
    public void populateUI(List<Food> foodList) {
        this.foodList = foodList;
        if (foodList.size() > 0) {
            final List<String> foodTypeList = new ArrayList<String>();
            final List<Integer> foodTimingList = new ArrayList<Integer>();
            for (Food food : foodList) {
                if(!foodTypeList.contains(food.getName())) {
                    foodTypeList.add(food.getName());
                }
                if(!foodTimingList.contains(food.getTiming())) {
                    foodTimingList.add(food.getTiming());
                }
            }

            //sort
            Collections.sort(foodTypeList, new Comparator<String>(){
                public int compare(String obj1, String obj2) {
                    return obj1.compareToIgnoreCase(obj2);
                }
            });
            Collections.sort(foodTimingList, new Comparator<Integer>(){
                public int compare(Integer obj1, Integer obj2) {
                    return obj1.compareTo(obj2);
                }
            });


            //end of sort
            ArrayAdapter<String> foodtypeAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, foodTypeList);
            foodtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            foodType.setAdapter(foodtypeAdapter);

            ArrayAdapter<Integer> foodtimingAdapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_list_item_1, foodTimingList);
            foodtimingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            foodTiming.setAdapter(foodtimingAdapter);
        }
    }

    @Override public void showLoading() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override public void hideLoading() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    private void searchFood() {
        Intent intent = new Intent(HomeActivity.this, FoodListActivity.class);
        intent.putExtra(FoodListActivity.SELECTED_FOOD_TYPE,foodType.getSelectedItem().toString());
        intent.putExtra(FoodListActivity.SELECTED_FOOD_TIMING,foodTiming.getSelectedItem().toString());
        startActivity(intent);
    }

}
