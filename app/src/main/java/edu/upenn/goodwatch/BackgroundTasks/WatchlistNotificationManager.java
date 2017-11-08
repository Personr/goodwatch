package edu.upenn.goodwatch.BackgroundTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationManager {

    private List<WatchlistItem> listenedForMovies;
    private String userID;

    public WatchlistNotificationManager() {
        this.listenedForMovies = new ArrayList<>();
    }

    public void registerMovie(WatchlistItem item) {
        listenedForMovies.add(item);
    }

    public void deregisterMovie(WatchlistItem item) {
        listenedForMovies.remove(item);
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Scan list of registered movies to see if anything new has happened, so that user can be
     * notified.
     */
    public void scanForUpdates() {

    }

}
