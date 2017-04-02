package com.example.reehams.goodreads;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.*;

import java.util.*;


/**
 * Created by rahulkooverjee on 3/30/17.
 */

public class HomeActivity extends SideBar {

    private ListView myReviewList;
    private ArrayList<String> myReviews = new ArrayList<>();
    static boolean firstTime = true;
    static boolean followersDone = false;
    static boolean userDone = false;
    final String userId = WelcomeActivity.userId1;


    final Set<Review> set = new TreeSet<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        followersDone = false;
        userDone = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        super.onCreateDrawer();
        myReviewList = (ListView) findViewById(R.id.yourReviewList);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myReviews);
        myReviewList.setAdapter(arrayAdapter2);
        myReviews.clear();
        myReviews.add("Loading...");
        arrayAdapter2.notifyDataSetChanged();
        if (firstTime) {
            firstTime = false;
            waitForFirebase();
        } else {
            waitForFirebase();
        }

    }

    public void helper(long delay) {
        final String userId = WelcomeActivity.userId1;
        final DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
        myReviewList = (ListView) findViewById(R.id.yourReviewList);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myReviews);
        myReviewList.setAdapter(arrayAdapter2);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myDatabase.child(userId).child("followingIds").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                                for (String s : l) {
                                    if (s.equals("null")) break;
                                    int commaIdx = s.indexOf(',');
                                    String id = s.substring(0, commaIdx);
                                    myDatabase.child(id).child("reviews").addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
                                                    for (HashMap<String, String> s : l) {
                                                        // TODO NEED TO PARSE THE STRING TO GET THE TITLE, RATING AND TEXT
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
                                                    displaySet();
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                }
                                makeFollowersTrue();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(
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
                                makeUserDoneTrue();
                                displaySet();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        }, delay); // wait a while basically
    }

    public void displaySet() {
        if (!followersDone) return;
        if (!userDone) return;
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myReviews);
        myReviewList.setAdapter(arrayAdapter2);
        myReviews.clear();
        if (set.isEmpty()) {
            myReviews.add("No reviews yet");
        }
        else {
            for (Review rev : set) {
                String displayText = rev.movieTitle + "\n" + rev.getStars() + "\n\"" + rev.reviewText + "\"";
                myReviews.add(displayText);
            }
        }
        arrayAdapter2.notifyDataSetChanged();
    }

    private void makeFollowersTrue() {
        followersDone = true;
    }

    private void makeUserDoneTrue() {
        userDone = true;
    }

    private void waitForFirebase() {
        final DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.child(userId).child("followingIds").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                        if (l == null) {
                            waitForFirebase();
                        }
                        else {
                            helper(0);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}

