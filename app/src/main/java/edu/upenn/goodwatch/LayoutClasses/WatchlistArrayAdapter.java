package edu.upenn.goodwatch.LayoutClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

import edu.upenn.goodwatch.BackgroundTasks.WatchlistItem;

/**
 * Created by Alex on 11/15/17.
 */

public class WatchlistArrayAdapter extends ArrayAdapter<WatchlistItem> {
    List<WatchlistItem> watchlistItems;

    public WatchlistArrayAdapter(@NonNull Context context, int resource, @NonNull List<WatchlistItem> objects) {
        super(context, resource, objects);
        this.watchlistItems = objects;
    }


}
