package edu.upenn.goodwatch.BackgroundTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationManager {

    private List<WatchlistItem> listenedForMovies;
    private String userID;
    private String updateMsg;

    public WatchlistNotificationManager() {
        this.listenedForMovies = new ArrayList<>();
        this.updateMsg = null;
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
    public String scanForUpdates() {
        // Get a reference to our database, and see if the user's list of movies has updated
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String msg;
        DatabaseReference moviesInWatchlist = db.child(userID).child("watchlist");
        moviesInWatchlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                updateMsg = lookupMovies(l);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return updateMsg;
    }

    /**
     * Lookup the movies on this user's watchlist to see if any other user's have done anything new
     * with them.
     * @param movies
     * @return
     */
    private String lookupMovies(List<HashMap<String, String>> movies) {
        return null;
    }

}
