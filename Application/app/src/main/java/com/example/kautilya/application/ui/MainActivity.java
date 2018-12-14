package com.example.kautilya.application.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.kautilya.application.App;
import com.example.kautilya.application.base.BaseActivity;
import com.example.kautilya.application.objects.Place;
import com.example.kautilya.application.adapters.PlaceAdapter;
import com.example.kautilya.application.service.PlaceService;
import com.example.kautilya.application.R;
import com.example.kautilya.application.databinding.ActivityMainBinding;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final int REMINDER_INTERVAL_SECONDS = 5;
    List<Place> placeString;

    private PlaceAdapter adapter;
    private int SYNC_FLEXTIME_SECONDS = 10;
    private FirebaseJobDispatcher firebaseJobDispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeString = new ArrayList<>();
        getViewBinding().data.setLayoutManager(new LinearLayoutManager(this));
        getViewBinding().data.setAdapter(adapter = new PlaceAdapter(MainActivity.this, placeString));
        Driver driver = new GooglePlayDriver(this);
        firebaseJobDispatcher = new FirebaseJobDispatcher(driver);

        setupFirebaseListeners();
        updateUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.log_out:
                firebaseJobDispatcher.cancel(App.PLACE_UPDATE_SERVICE);
                FirebaseAuth.getInstance().signOut();
                updateUI();
                break;
        }
        return true;
    }

    private void setupFirebaseListeners() {
        App.getmDbInstance().placeDao().getLivePlaceData().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                adapter.updateData(places);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    void updateUI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            killJobService();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {
            startJobService();
        }

    }

    private void startJobService() {

        Job constraintReminderJob = firebaseJobDispatcher.newJobBuilder()
                .setTag(App.PLACE_UPDATE_SERVICE)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setService(PlaceService.class)
                .setReplaceCurrent(true)
                .build();

        firebaseJobDispatcher.schedule(constraintReminderJob);
    }

    private void killJobService() {

    }


    public static class MyDiffCallback extends DiffUtil.Callback {

        List<Place> oldPlaces;
        List<Place> newPlaces;

        public MyDiffCallback(List<Place> newPlaces, List<Place> oldPlaces) {
            this.newPlaces = newPlaces;
            this.oldPlaces = oldPlaces;
        }

        @Override
        public int getOldListSize() {
            return oldPlaces.size();
        }

        @Override
        public int getNewListSize() {
            return newPlaces.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPlaces.get(oldItemPosition).getId() == newPlaces.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPlaces.get(oldItemPosition).equals(newPlaces.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            //you can return particular field for changed item.
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}
