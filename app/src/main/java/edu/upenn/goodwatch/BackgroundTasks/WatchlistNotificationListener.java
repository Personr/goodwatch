package edu.upenn.goodwatch.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

import edu.upenn.goodwatch.HomeActivity;

/**
 * Created by Alex on 11/11/17.
 */

public class WatchlistNotificationListener implements ValueEventListener {

    private String movieId;
    private StringBuffer sb;
    private String title;
    private CountDownLatch latch;
    //TODO pass in activity
    private Activity callingActivity;

    public WatchlistNotificationListener(String movieId, String title, StringBuffer sb, CountDownLatch latch) {
        this.movieId = movieId;
        this.sb = sb;
        this.title = title;
        this.latch = latch;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Add the movie to the database if it's not already there
        if (dataSnapshot == null) {
            Movie m = new Movie();
            FirebaseDatabase.getInstance().getReference().child(movieId).setValue(m);
        // Otherwise, notify user of changes
        } else {
            sb.append(title).append("\n");
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = String.format("New activity for %s in your watchlist!", title);
                    Toast.makeText(callingActivity, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Count down so that the WatchlistNotificationManager knows this is done
        latch.countDown();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
