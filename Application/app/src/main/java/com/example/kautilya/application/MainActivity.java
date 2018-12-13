package com.example.kautilya.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kautilya.application.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    List<String> placeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeString = new ArrayList<>();
        getViewBinding().data.setLayoutManager(new LinearLayoutManager(this));
        getViewBinding().data.setAdapter(new PlaceAdapter());

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {


        @NonNull
        @Override
        public PlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PlaceHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_place, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceHolder placeHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return placeString.size();
        }

        class PlaceHolder extends RecyclerView.ViewHolder {


            public PlaceHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
