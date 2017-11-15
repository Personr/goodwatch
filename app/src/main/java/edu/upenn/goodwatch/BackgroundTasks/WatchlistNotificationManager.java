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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationManager {

    private List<WatchlistItem> listenedForMovies;
    private String userID;
    private String updateMsg;
    private boolean updatesExist;
    private CountDownLatch latch;
    private Semaphore startSem;
    private StringBuffer sb;

    public WatchlistNotificationManager() {
        this.listenedForMovies = new ArrayList<>();
        this.updatesExist = false;
        this.startSem = new Semaphore(1);
        sb = new StringBuffer("New reviews have been added to the following movies on your watchlist:\n");
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
        // Acquire the semaphore to indicate that we are waiting for first db query to watchlist
        startSem.acquireUninterruptibly();
        // Get a reference to our database, and see if the user's list of movies has updated
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
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
        this.updatesExist = false;
        updateMsg = null;
        return retval;
    }

    public String waitForUpdate() {
        Log.e("Manager", "Starting to wait");
        sb = new StringBuffer("New reviews have been added to the following movies on your watchlist:\n");
        try {
            Log.e("Manager", "Calling scan");
            scanForUpdates();
            Log.e("Manager", "Waiting for semaphore");
            // Wait for the semaphore
            startSem.acquire();
            Log.e("Manager", "Got the semaphore, waiting for latch");
            // Wait for the latch
            latch.await();
            Log.e("Manager", "Latch is done");
        } catch (InterruptedException e) {
            startSem.release();
            Thread.currentThread().interrupt();
        }
        startSem.release();
        return sb.toString();
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
        // Create a latch to indicate whether work is done for each movie in watchlist
        this.latch = new CountDownLatch(watchlistItems.size());
        // Release the semaphore
        startSem.release();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        for (final String movie: watchlistItems) {
            String[] watchlistItem = movie.split(",");
            if (watchlistItem != null && watchlistItem.length >= 2) {
                // The user's watchlist stores each movie as "MovieTitle,HashCode"
                final String id = movie.split(",")[0];
                final String title = movie.split(",")[1];
                // Add an event listener to every movie. The listener does all the work from here.
                db.child(id).addListenerForSingleValueEvent(new WatchlistNotificationListener(id, title, sb, latch));
            }
        }
    }

}
