package edu.upenn.goodwatch;

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

    public ReviewFormValueEventListener(Review review1, DatabaseReference myDatabase, String userId) {
        this.myDatabase = myDatabase;
        this.review1 = review1;
        this.userId = userId;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Get user value
        List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
        if (l.get(0).get("movieId").equals("null")) {
            l.remove(0);
        }
        l.add(review1.getMapping());
        myDatabase.child(userId).child("reviews").setValue(l);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
