package edu.upenn.goodwatch.Listeners;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.upenn.goodwatch.HomeActivity;
import edu.upenn.goodwatch.Review;
import edu.upenn.goodwatch.User;

/**
 * Created by raph on 15/11/17.
 */

public class ReviewsListener implements ValueEventListener {

    private DatabaseReference myDatabase;
    private String userId;
    private Set<Review> userReviews;
    private HomeActivity activity;

    public ReviewsListener(DatabaseReference myDatabase, String userId, Set<Review> userReviews, HomeActivity activity) {
        this.myDatabase = myDatabase;
        this.userId = userId;
        this.userReviews = userReviews;
        this.activity = activity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        userReviews.clear();
        List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();
        for (HashMap<String, String> s : l) {
            if (s != null) {
                String movieId = s.get("movieId");
                if (movieId.equals("null")) continue;
                String movieTitle = s.get("movieTitle");
                String rating = s.get("rating");
                String reviewText = s.get("reviewText");
                if (reviewText.length() > 175) {
                    reviewText = reviewText.substring(0, 175) + "...";
                }
                String time = s.get("time");
                final Review r = new Review(movieId, rating, reviewText, movieTitle, time);
                myDatabase.child(userId).addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                HashMap<String, String> userFields = (HashMap<String, String>) dataSnapshot.getValue();
                                User user = new User(userFields.get("name"), userFields.get("email"), userFields.get("id"), userFields.get("photoUrl"));
                                r.setUser(user);
                                activity.refresh();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
                userReviews.add(r);
            }

        }
        activity.displaySet();
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

}
