package com.example.reehams.goodreads;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by reehams on 2/17/17.
 */

public class MyAccountActivity extends SideBar {
    TextView email;
    TextView gender;
    TextView userName;
    ProfilePictureView image;
    private ListView userReviewsView;
    private ArrayList<String> userReviewsList = new ArrayList<>();
    final Set<Review> set = new TreeSet<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.my_account);
        super.onCreateDrawer();
        email = (TextView) findViewById(R.id.email);
        email.setText("Email:" + " " + WelcomeActivity.email);
        gender = (TextView) findViewById(R.id.gender);
        gender.setText("Gender:" + " " +WelcomeActivity.gender);
        userName = (TextView) findViewById(R.id.userName);
        userName.setText("Name:" + " " + WelcomeActivity.facebookName);
        image = (ProfilePictureView) findViewById(R.id.image);
        image.setPresetSize(ProfilePictureView.NORMAL);
        image.setProfileId(WelcomeActivity.profilePicId);
        userReviewsView = (ListView) findViewById(R.id.userReviewsList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userReviewsList);
        userReviewsView.setAdapter(arrayAdapter);
        userReviewsList.clear();
        userReviewsList.add("Loading...");
        arrayAdapter.notifyDataSetChanged();
        DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.child(WelcomeActivity.userId1).child("reviews").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //List l = dataSnapshot.getValue(List.class);
                        List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                        for (HashMap<String, String> s : l) {
                            String movieId = s.get("movieId");
                            if (movieId.equals("null")) continue;
                            String movieTitle = s.get("movieTitle");
                            String rating = s.get("rating");
                            String reviewText = s.get("reviewText");
                            if (reviewText.length() > 175) {
                                reviewText = reviewText.substring(0, 175) + "...";
                            }
                            String time = s.get("time");
                            Review r = new Review(movieId, rating, reviewText, movieTitle, time);
                            set.add(r);
                        }
                        userReviewsList.clear();
                        if (set.isEmpty()) {
                            userReviewsList.add("You have no reviews");
                        }
                        else {
                            for (Review rev : set) {
                                String displayText = rev.movieTitle + "\n" + rev.getStars() + "\n\"" + rev.reviewText + "\"";
                                userReviewsList.add(displayText);
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //waitForFirebase();
    }

    protected void watchlistOnButtonPressed(View view) {
        Intent i = new Intent(this,WatchlistActivity.class);
        startActivity(i);
    }

    protected void whoIamFollowing(View view) {
        Intent i = new Intent(this, FollowingListActivity.class);
        i.putExtra("user_id", WelcomeActivity.userId1);
        startActivity(i);
    }
    protected void myFollowers(View view) {
        Intent i = new Intent(this, FollowerListActivity.class);
        i.putExtra("user_id", WelcomeActivity.userId1);
        startActivity(i);
    }
}
