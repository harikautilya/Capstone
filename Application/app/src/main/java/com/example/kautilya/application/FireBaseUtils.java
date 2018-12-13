package com.example.kautilya.application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseUtils {


    private static FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }


    public static DatabaseReference getPlaceDetails() {
        return getFirebaseDatabase().getReference("udacity").child("place");
    }


    public static Task<Void> setBookingReference(String UUID, long placeId,boolean value) {
        return getFirebaseDatabase().getReference("udacity").child("User").child(UUID).child(placeId + "").setValue(true);
    }

    public static DatabaseReference getBookingReference(String UUID, long placeId) {
        return getFirebaseDatabase().getReference("udacity").child("User").child(UUID).child(placeId + "");
    }
}
