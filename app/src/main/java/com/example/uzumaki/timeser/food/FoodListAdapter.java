package com.example.uzumaki.timeser.food;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.uzumaki.timeser.FoodApplication;
import com.example.uzumaki.timeser.model.Food;

import java.util.List;

import ln.designdemo.R;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {

    private List<Food> foodList;
    ImageLoader imageLoader = FoodApplication.getInstance().getImageLoader();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, timing;
        public NetworkImageView thumbNail;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            timing = view.findViewById(R.id.timing);
            thumbNail = view.findViewById(R.id.thumbnail);
        }
    }


    public FoodListAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    public void setItems(List<Food> foodList) {
        this.foodList = foodList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_list_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.thumbNail.setImageUrl(food.getImageUrl(), imageLoader);
        holder.title.setText(food.getName());
        holder.timing.setText(String.valueOf(food.getTiming()));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
