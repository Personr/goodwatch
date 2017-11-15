package edu.upenn.goodwatch.BackgroundTasks;

/**
 * The state of a movie on the user's watchlist
 * Created by Alex on 11/8/17.
 */

public class WatchlistItem {

    private String movieTitle;
    private String averageRating;

    public WatchlistItem(String movieTitle) {
        this.movieTitle = movieTitle;
        this.averageRating = "Be the first to review!";
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s", movieTitle, averageRating);
    }
}
