package edu.upenn.goodwatch;

import java.util.HashMap;

/**
 * Created by Ameya on 3/28/17.
 */

public class Review implements Comparable<Review> {
//For firebase reviews
    String movieId;
    String movieTitle;
    String reviewText;
    String rating;
    String time;
    User user;

    public Review() {
        // No args constructor needed for DataSnapshot.getValue()
    }

    public Review(String s) {
        this.movieId = s;
        this.reviewText = s;
        this.rating = "0";
        this.movieTitle = s;
        this.time = System.currentTimeMillis() + "";
    }

    public Review(String movieId, String rating, String reviewText, String movieTitle) {
        this.movieId = movieId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.movieTitle = movieTitle;
        this.time = System.currentTimeMillis() + "";
    }

    public Review(String movieId, String rating, String reviewText, String movieTitle, String time) {
        this.movieId = movieId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.movieTitle = movieTitle;
        this.time = time;
    }

    public String toString() {
        return "\"" + time + ",\"" + movieId + "\",\"" + movieTitle + "\",\"" + getStars() + "\",\"" + reviewText + "\"";
    }

    public HashMap<String, String> getMapping() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("movieId", movieId);
        map.put("movieTitle", movieTitle);
        map.put("rating", rating);
        map.put("reviewText", reviewText);
        map.put("time", time);
        return map;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getRating() {
        return rating;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getTime() {
        return time;
    }

    public String getStars() {
        String blackStar = "★";
        String whiteStar = 	"☆";
        String output = "";
        int numStars = Integer.parseInt(rating);
        int i;
        for (i = 0; i < numStars; i++) {
            output += blackStar;
        }
        for (i = i; i < 10; i++) {
            output += whiteStar;
        }
        return output;
    }

    @Override
    public int compareTo(Review r) {
        return r.time.compareTo(this.time);
    }

    public String getUserName() {
        return (user == null) ? "Someone" : user.getName();
    }
}
