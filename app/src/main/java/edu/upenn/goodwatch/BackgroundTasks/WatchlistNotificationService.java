package edu.upenn.goodwatch.BackgroundTasks;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationService extends IntentService {

    private Handler mHandler;
    private WatchlistNotificationManager notificationManager;

    public WatchlistNotificationService() {
        super("WatchlistNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        notificationManager = new WatchlistNotificationManager();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String userID = (String) intent.getExtras().get("userID");
        notificationManager.setUserID(userID);
        String msg = String.format("User ID passed to service is %s", userID);
        sendToastToMainThread(msg);
    }

    private void sendToastToMainThread(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WatchlistNotificationService.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
