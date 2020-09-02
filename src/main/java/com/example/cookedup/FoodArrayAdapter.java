package com.example.cookedup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FoodArrayAdapter extends ArrayAdapter {

    public FoodArrayAdapter(Context context, ArrayList<String> resource) {
        super(context,0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView =convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_food_array_adapter, parent, false);
        }
        String curr = (String) getItem(position);
        TextView textView = listItemView.findViewById(R.id.FoodListAdapter);
        textView.setText(curr);
        return listItemView;
    }

}