package edu.upenn.goodwatch.BackgroundTasks;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Alex on 11/11/17.
 */

public class WatchlistNotificationListener implements ValueEventListener {

    private String movieId;
    private StringBuffer sb;
    private String title;
    private CountDownLatch latch;
    private Context context;
    private Handler h;

    public WatchlistNotificationListener(String movieId, String title, StringBuffer sb, CountDownLatch latch, Context caller) {
        this.movieId = movieId;
        this.sb = sb;
        this.title = title;
        this.latch = latch;
        this.context = caller;
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
                sendToastToMainThread(msg);
                sb.append(msg);
            }
            // Set a value here, so that the Firebase doesn't erase the now empty mailbox
            userMbox.getRef().setValue(false);
//            context.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String msg = String.format("New activity for %s in your watchlist!", title);
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                }
//            });
        }
        // Count down so that the WatchlistNotificationManager knows this is done
        latch.countDown();
    }

    private void sendToastToMainThread(final String msg) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
