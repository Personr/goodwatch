package com.example.reehams.goodreads;

/**
 * Created by Ameya on 3/28/17.
 */

public class Review {
//For firebase reviews
    String movieId;
    String movieTitle;
    String reviewText;
    String rating;


    public Review(String movieId, String rating, String reviewText, String movieTitle) {
        this.movieId = movieId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.movieTitle = movieTitle;
    }

    public String toString() {
        return movieId + "," + movieTitle + "," + rating + "," + reviewText;
    }

}
