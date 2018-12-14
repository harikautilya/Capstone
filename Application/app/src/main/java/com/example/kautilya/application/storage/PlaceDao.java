package com.example.kautilya.application.storage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.kautilya.application.objects.Place;

import java.util.List;

@Dao
public interface PlaceDao {


    @Query("SELECT * FROM place")
    LiveData<List<Place>> getLivePlaceData();


    @Query("SELECT * FROM place")
    List<Place> getPlaceData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertPlace(List<Place> places);
}
