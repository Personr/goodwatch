package edu.upenn.goodwatch.BackgroundTasks;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alex on 11/11/17.
 */

public class WatchlistNotificationListener implements ValueEventListener {

    private Context context;

    public WatchlistNotificationListener(Context caller) {
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
            }
            // Set a value here, so that the Firebase doesn't erase the now empty mailbox
            userMbox.getRef().setValue(false);
        }
    }

    private void sendToastToMainThread(final String msg) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
