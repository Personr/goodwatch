package edu.upenn.goodwatch.BackgroundTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationManager {

    private List<WatchlistItem> listenedForMovies;
    private String userID;
    private String updateMsg;
    private boolean updatesExist;

    public WatchlistNotificationManager() {
        this.listenedForMovies = new ArrayList<>();
        this.updateMsg = null;
        this.updatesExist = false;
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
     * Scan list of registered movies to see if anything new has happened.  If something
     * new has happened, return a message describing the new events
     */
    public void scanForUpdates() {
        // Get a reference to our database, and see if the user's list of movies has updated
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String msg;
        DatabaseReference moviesInWatchlist = db.child(userID).child("watchlist");
        moviesInWatchlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList movieItems = (ArrayList<String>) dataSnapshot.getValue();
                lookupMovies(movieItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * After scanning for updates, this method allows the caller to get the update message to be
     * displayed to the user.
     * @return
     */
    public String getUpdateMsg() {
        String retval = null;
        if (updatesExist) {
            retval = updateMsg;
        }
        setUpdatesExist(false);
        updateMsg = null;
        return retval;
    }



    private void setUpdatesExist(boolean val) {
        updatesExist = val;
    }


    /**
     * Lookup the movies on this user's watchlist to see if any other user's have done anything new
     * with them.
     * @param watchlistItems a List of primitives from Firebase for this user's watchlist
     * @return
     */
    private void lookupMovies(List<String> watchlistItems) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final StringBuffer sb = new StringBuffer("New reviews have been added to the following movies on your watchlist:\n");
        for (final String movie: watchlistItems) {
            // The user's watchlist stores each movie as "MovieTitle,HashCode"
            final String id = movie.split(",")[0];
            final String title = movie.split(",")[1];
            db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Map<String, String>> reviews = (ArrayList) dataSnapshot.getValue();
                    // There will be no entry in the Firebase if nobody has reviewed this movie yet
                    if (reviews != null) {
                        setUpdatesExist(true);
                        sb.append(title).append("\n");
                        updateMsg = sb.toString();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

}
