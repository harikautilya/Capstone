package com.example.kautilya.application;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.kautilya.application.storage.AppDatabase;


public class App extends Application {

    public static String PLACE_UPDATE_SERVICE = "place_server";
    private static AppDatabase mDbInstance;

    public static AppDatabase initDb(Context context) {
        if (mDbInstance == null)
            synchronized (App.class) {
                if (mDbInstance == null)
                    mDbInstance = Room.databaseBuilder(context,
                            AppDatabase.class, "database-name").build();
                return mDbInstance;
            }
        return mDbInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initDb(getApplicationContext());

    }


    public static AppDatabase getmDbInstance() {
        return mDbInstance;
    }
}
