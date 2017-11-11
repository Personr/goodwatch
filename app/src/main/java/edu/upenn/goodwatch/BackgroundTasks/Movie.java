package edu.upenn.goodwatch.BackgroundTasks;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 11/11/17.
 */

@IgnoreExtraProperties
class Movie {

    public List<Object> reviews;

    public Movie() {
        // Default constructor required for use with DataSnapshot.getValue()
        this.reviews = new ArrayList<>();
    }


    public List<Object> getReviews() {
        return reviews;
    }
}
