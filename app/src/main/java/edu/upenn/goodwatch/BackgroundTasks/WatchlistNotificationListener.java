package edu.upenn.goodwatch.BackgroundTasks;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alex on 11/11/17.
 */

public class WatchlistNotificationListener implements ValueEventListener {

    private String movieId;
    private StringBuffer sb;
    private String title;

    public WatchlistNotificationListener(String movieId, String title, StringBuffer sb) {
        this.movieId = movieId;
        this.sb = sb;
        this.title = title;
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
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
