package edu.upenn.goodwatch;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MertZ on 31/10/2017.
 */

public class ReviewFormValueEventListener implements ValueEventListener {
    private Review review1;
    private DatabaseReference myDatabase;
    private String userId;
    private final String DEBUG_TAG = getClass().getSimpleName();
    private String ts;


    public ReviewFormValueEventListener(Review review1, DatabaseReference myDatabase, String userId, String ts) {
        this.myDatabase = myDatabase;
        this.review1 = review1;
        this.userId = userId;
        this.ts = ts;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Get user value
        List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
        if (l != null) {
            if (l.get(0) != null) {
                if (l.get(0).get("movieId") != null) {
                    if (l.get(0).get("movieId").equals("null")) {
                        l.remove(0);
                    }
                }
            }

            List<HashMap<String, String>> lcopy = new ArrayList<HashMap<String, String>>();
            int ln = l.size();
            for (int i = 0; i < ln; i++) {
                HashMap<String, String> current = l.get(i);
                if (!current.get("time").equals(ts)) {
                    lcopy.add(current);
                }
            }

            if (!ts.equals(review1.time)) {
                lcopy.add(review1.getMapping());
            }
            myDatabase.child(userId).child("reviews").setValue(lcopy);
        }
        else {

            l = new ArrayList<HashMap<String, String>>();
            if (!ts.equals(review1.time)) {
                l.add(review1.getMapping());
                myDatabase.child(userId).child("reviews").setValue(l);
            }
        }
        // Notify people with this movie in their watchlist that something new has happened
        final String postcenterPath = review1.getMovieId() + "-postcenter";
        myDatabase.child(postcenterPath).addListenerForSingleValueEvent(new WatchlistNotifyValueEventListener(userId, review1));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
