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

public class ReviewFormValueMovieEventListener implements ValueEventListener{
    private DatabaseReference myDatabase;
    private String movieId;
    private Review review1;

    public  ReviewFormValueMovieEventListener(DatabaseReference myDatabase, String movieId, Review review1) {
        this.myDatabase = myDatabase;
        this.movieId = movieId;
        this.review1 = review1;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.child(movieId).exists()) {
            myDatabase.child(movieId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                            l.add(review1.getMapping());
                            myDatabase.child(movieId).setValue(l);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            List<HashMap<String, String>> l = new ArrayList<HashMap<String, String>>();
            l.add(review1.getMapping());
            myDatabase.child(movieId).setValue(l);
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
