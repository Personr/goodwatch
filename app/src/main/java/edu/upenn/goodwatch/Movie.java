package edu.upenn.goodwatch;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;

/**
 * Created by raph on 19/11/17.
 */

public class Movie {

    private String name;
    private String releaseDate;
    private String posterURL;
    private String description;
    private String runtime;
    private String genres;
    private String director;
    private String rating;
    private String actors;
    
    public  Movie(String movieId, String DEBUG_TAG, Context context) {

        name = Messages.getMessage(context, "movie.noInfo");
        releaseDate = Messages.getMessage(context, "movie.noInfo");
        posterURL = Messages.getMessage(context, "movie.noInfo");
        description = Messages.getMessage(context, "movie.noInfo");
        runtime = Messages.getMessage(context, "movie.noInfo");
        genres  = Messages.getMessage(context, "movie.noInfo");
        director = Messages.getMessage(context, "movie.noInfo");
        rating = Messages.getMessage(context, "movie.noInfo");
        actors = Messages.getMessage(context, "movie.noInfo");
        
        try {
            String url = Config.getMovieInfoUrl(context, movieId);
            JSONObject response = new MovieBackend().queryThemoviedb(url, DEBUG_TAG, context);
            String responseString = response.toString();

            // Set movie info
            name = response.getString("original_title");
            releaseDate = response.getString("release_date");
            //Non available information
            runtime = response.getString("runtime");
            posterURL = Config.getPosterBaseUrl(context) + response.getString("poster_path");

            if (posterURL.equalsIgnoreCase(Messages.getMessage(context, "movie.noInfo"))) {
                posterURL = Config.getDefaultPoster(context);
            }
            description = response.getString("overview");
            director = CustomJSONParser.getDirector(responseString);
            rating = CustomJSONParser.getRating(responseString);
            actors = CustomJSONParser.getActors(responseString);
            genres = CustomJSONParser.getGenre(responseString);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, Messages.getMessage(context, "movie.omdbError"), e);
        }
    }

    public String getName() {
        return name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getDescription() {
        return description;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenres() {
        return genres;
    }

    public String getDirector() {
        return director;
    }

    public String getRating() {
        return rating;
    }

    public String getActors() {
        return actors;
    }
}
