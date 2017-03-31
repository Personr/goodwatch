package com.example.reehams.goodreads;

/**
 * Created by Ameya on 3/28/17.
 */

public class Review {
//For firebase reviews
    String movieId;
    String reviewText;
    String rating;


    public Review(String movieId, String rating, String reviewText) {
        this.movieId = movieId;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public String toString() {
        return movieId + "," + rating + "," + reviewText;
    }

}
