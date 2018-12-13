package com.example.kautilya.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.kautilya.application.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    List<Place> placeString;
    private ValueEventListener valueEventListener;
    private DatabaseReference localreference;
    private PlaceAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeString = new ArrayList<>();
        getViewBinding().data.setLayoutManager(new LinearLayoutManager(this));
        getViewBinding().data.setAdapter(adapter = new PlaceAdapter(MainActivity.this, placeString));
        localreference = FireBaseUtils.getPlaceDetails();
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
                FirebaseAuth.getInstance().signOut();
                updateUI();
                break;
        }
        return true;
    }

    private void setupFirebaseListeners() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Place> places = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Place place = child.getValue(Place.class);
                    places.add(place);
                }

                adapter.updateData(places);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();

            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        localreference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        localreference.orderByValue().addValueEventListener(valueEventListener);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    void updateUI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

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
