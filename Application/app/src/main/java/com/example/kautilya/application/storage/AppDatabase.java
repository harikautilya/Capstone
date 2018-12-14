package com.example.kautilya.application.storage;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.kautilya.application.objects.Place;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}
