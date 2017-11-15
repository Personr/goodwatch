package edu.upenn.goodwatch.BackgroundTasks;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Class to accept messages from the main UI thread to be sent to the WatchlistNotificationManager
 * Created by Alex on 11/8/17.
 */

class WatchlistMessageHandler extends Handler {

    private WatchlistNotificationManager manager;

    WatchlistMessageHandler(Looper looper, WatchlistNotificationManager manager) {
        super(looper);
        this.manager = manager;
    }

    @Override
    public void handleMessage(Message message) {
    }


}
