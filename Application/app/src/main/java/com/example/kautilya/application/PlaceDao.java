package com.example.kautilya.application;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {


    @Query("SELECT * FROM place")
    LiveData<List<Place>> getPlaceData();
}
