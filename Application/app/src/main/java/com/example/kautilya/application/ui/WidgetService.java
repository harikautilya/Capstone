package com.example.kautilya.application.ui;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.kautilya.application.App;

public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {

        final ListProvider provider = new ListProvider(this.getApplicationContext(), intent, App.getmDbInstance().placeDao().getPlaceData());

        return provider;
    }


}
