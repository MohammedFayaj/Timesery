package com.example.uzumaki.timeser.home;

import com.example.uzumaki.timeser.model.Food;

import java.util.List;


public interface HomeContract {

    interface Presenter {
        void onCreate();
    }

    interface View {
        void showLoading();
        void hideLoading();
        void populateUI(List<Food> foodList);
    }
}
