package edu.upenn.goodwatch.BackgroundTasks;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Alex on 11/8/17.
 */

public class WatchlistNotificationService extends IntentService {

    private WatchlistNotificationManager notificationManager;

    public WatchlistNotificationService() {
        super("WatchlistNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = new WatchlistNotificationManager(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String userId = (String) intent.getExtras().get("userId");
        notificationManager.setUserId(userId);
        notificationManager.scanForUpdates();
    }
}
