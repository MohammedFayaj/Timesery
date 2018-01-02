package com.example.uzumaki.timeser.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    private Toolbar toolbar;
    private TextView errorMessageView;
    private List<Food> foodList = new ArrayList<Food>();

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
        } else {
            errorMessageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
