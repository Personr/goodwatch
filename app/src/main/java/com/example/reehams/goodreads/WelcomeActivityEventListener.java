package com.example.reehams.goodreads;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MertZ on 01/11/2017.
 */

public class WelcomeActivityEventListener implements ValueEventListener {
    private String userId1;
    private String facebookName;
    private String email;
    private DatabaseReference myDatabase;

    public WelcomeActivityEventListener(String userId, String facebookName, String email, DatabaseReference myDatabase) {
        this.userId1 = userId;
        this.facebookName = facebookName;
        this.email = email;
        this.myDatabase = myDatabase;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<String> watchlist = (ArrayList<String>) dataSnapshot.child(userId1).child("watchlist").getValue();
        List<Review> reviews = (ArrayList<Review>) dataSnapshot.child(userId1).child("reviews").getValue();
        List<String> following = (ArrayList<String>) dataSnapshot.child(userId1).child("followingIds").getValue();
        List<String> followers = (ArrayList<String>) dataSnapshot.child(userId1).child("followerIds").getValue();
        if (watchlist == null) {
            watchlist = new ArrayList<String>();
            watchlist.add("null");
        }
        if (reviews == null) {
            reviews = new ArrayList<Review>();
            // reviews.add("null");
            reviews.add(new Review(("null")));

        }
        if (following == null) {
            following = new ArrayList<String>();
            following.add("null");
        }
        if (followers == null) {
            followers = new ArrayList<String>();
            followers.add("null");
        }
        User user = new User(facebookName, email, userId1, watchlist, reviews, following, followers);
        myDatabase.child(userId1).setValue(user);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
