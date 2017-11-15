package edu.upenn.goodwatch.BackgroundTasks;

import android.app.Activity;
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
    public void onDataChange(DataSnapshot userMbox) {
        // Should be impossible, but if user doesn't have a mailbox for movie on watchlist, add one
        if (userMbox == null) {
            Log.w(getClass().getSimpleName(), "User finds watchlist notification mailbox doesn't exist");
            userMbox.getRef().setValue(false);
        }
        // Otherwise, consume all the notifications
        else if (userMbox.getChildrenCount() > 0) {
            for (DataSnapshot notification: userMbox.getChildren()) {
                String msg = notification.getValue(String.class);
                notification.getRef().removeValue();
                sb.append(msg);
            }
            // Set a value here, so that the Firebase doesn't erase the now empty mailbox
            userMbox.getRef().setValue(false);
//            callingActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String msg = String.format("New activity for %s in your watchlist!", title);
//                    Toast.makeText(callingActivity, msg, Toast.LENGTH_SHORT).show();
//                }
//            });
        }
        // Count down so that the WatchlistNotificationManager knows this is done
        latch.countDown();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
