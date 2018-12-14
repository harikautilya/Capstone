package com.example.kautilya.application;

import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlaceService extends JobService {
    private ValueEventListener valueEventListener;
    private DatabaseReference localreference;


    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        localreference = FireBaseUtils.getPlaceDetails();

        if (App.getmDbInstance() == null) {
            App.initDb(getApplicationContext());
        }
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Place> places = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Place place = child.getValue(Place.class);
                    places.add(place);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();

            }
        };

        localreference.addValueEventListener(valueEventListener);
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        localreference.removeEventListener(valueEventListener);
        return false;
    }
}
