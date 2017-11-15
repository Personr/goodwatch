package edu.upenn.goodwatch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import edu.upenn.goodwatch.Review;

/**
 * Created by Alex on 11/15/17.
 */

public class WatchlistNotifyValueEventListener implements ValueEventListener {

    private String userId;
    private Review review1;

    public WatchlistNotifyValueEventListener(String userId, Review review1) {
        this.userId = userId;
        this.review1 = review1;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // If there is a postcenter at all, tell each listening user about a change
        if (dataSnapshot.getValue() != null) {
            String msg = String.format("%s wrote a review for %s", review1.getUserName(), review1.getMovieTitle());
            for (DataSnapshot userMbox : dataSnapshot.getChildren()) {
                // Don't notify yourself
                if (!userMbox.getKey().equals(userId)) {
                    userMbox.getRef().child(userId).setValue(msg);
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
