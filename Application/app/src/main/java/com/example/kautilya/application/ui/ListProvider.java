package com.example.kautilya.application.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.kautilya.application.R;
import com.example.kautilya.application.objects.Place;

import java.util.List;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<Place> places;

    public ListProvider(Context context, Intent intent, List<Place> places) {
        this.context = context;
        this.places = places;
    }


    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);

        remoteView.setTextViewText(R.id.heading, places.get(position).getName());
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteView.setOnClickPendingIntent(R.id.view_complete, pendingIntent);


        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
    }

    public void updateData(List<Place> places) {
        this.places = places;
    }
}