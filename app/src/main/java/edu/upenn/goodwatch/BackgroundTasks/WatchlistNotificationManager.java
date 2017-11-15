package edu.upenn.goodwatch.BackgroundTasks;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationManager {

    private String userId;
    private Context context;

    public WatchlistNotificationManager(Context callingActivity) {
        this.context = callingActivity;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    /**
     * Scan list of registered movies to see if anything new has happened.  If something
     * new has happened, return a message describing the new events
     */
    public void scanForUpdates() {
        // Get a reference to our database, and see if the user's list of movies has updated
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(userId).child("watchlist").addListenerForSingleValueEvent(new ValueEventListener() {
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
     * Lookup the movies on this user's watchlist to see if any other user's have done anything new
     * with them.
     * @param watchlistItems a List of primitives from Firebase for this user's watchlist
     * @return
     */
    private void lookupMovies(List<String> watchlistItems) {
        if (watchlistItems == null) {
            return;
        }
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        for (final String entry: watchlistItems) {
            if (entry != null) {
                // The user's watchlist stores each movie as "MovieTitle,HashCode"
                String[] elts = entry.split(",");
                if (elts.length < 2) {
                    continue;
                }
                final String id =elts[0];
                // Add an event listener for User's notification mailbox for this movie
                String userMbox = id + "-postcenter/" + userId;
                db.child(userMbox).addListenerForSingleValueEvent(new WatchlistNotificationListener(context));
            }
        }
    }

}
