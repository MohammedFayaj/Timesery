package com.example.uzumaki.timeser.home;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.uzumaki.timeser.Constants;
import com.example.uzumaki.timeser.FoodApplication;
import com.example.uzumaki.timeser.Util.AppUtils;
import com.example.uzumaki.timeser.model.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View view ;
    private List<Food> foodList = new ArrayList<Food>();
    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        view.showLoading();
        JsonObjectRequest movieReq = new JsonObjectRequest(Constants.SERVICE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        view.hideLoading();
                        if (response != null) {
                            try {
                                if (response.has("food") && response.get("food") instanceof JSONArray) {
                                    JSONArray foodArray = response.getJSONArray("food");
                                    for (int i = 0; i < foodArray.length(); i++) {
                                        JSONObject obj = foodArray.getJSONObject(i);
                                        Food food = new Food();
                                        food.setId(obj.getInt("id"));
                                        food.setName(obj.getString("name"));
                                        food.setImageUrl(obj.getString("image"));
                                        food.setTiming(obj.getInt("timing"));
                                        foodList.add(food);
                                    }

                                }
                                AppUtils.responseMap.put(AppUtils.FOOD_RESPONSE,foodList);
                                view.populateUI(foodList);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                view.hideLoading();

            }
        });

        // Adding request to request queue
        FoodApplication.getInstance().addToRequestQueue(movieReq);
    }
}
