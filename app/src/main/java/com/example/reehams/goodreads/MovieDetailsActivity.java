package com.example.reehams.goodreads;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by rahulkooverjee on 3/9/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity);
        // Variables to save movie data
        String name = "n/a";
        String releaseDate = "n/a";
        String posterURL = "n/a";
        String description = "n/a";
        String runtime = "n/a";
        String genres  = "n/a";
        String director = "n/a";
        String rating = "n/a";
        String actors = "n/a";
        try {
            // Get movie ID from intent
            String id = getIntent().getStringExtra("JSON_Data");
            // Get movie details in JSON object
            String[] queryArr = new String[1];
            queryArr[0] = "http://www.omdbapi.com/?i=" + id;
            AsyncTask search = new MovieBackend().execute(queryArr);
            JSONObject json = null;
            json = (JSONObject) search.get();
            // Set movie info
            name = json.get("Title").toString();
            releaseDate = json.get("Released").toString();
            runtime = json.get("Runtime").toString();
            posterURL = json.get("Poster").toString();
            if (posterURL.equalsIgnoreCase("n/a")) {
                posterURL = "http://s3.amazonaws.com/static.betaeasy.com/screenshot/456/456-25984-14192637741419263774.42.jpeg";
            }
            description = json.get("Plot").toString();
            director = getDirectorString(json.get("Director").toString());
            rating = json.get("imdbRating").toString();
            actors = getActorString(json.get("Actors").toString());
            genres = json.get("Genre").toString();
        } catch (Exception e) {
            Toast.makeText(MovieDetailsActivity.this, e + "", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Set movie info to view
        ((TextView) findViewById(R.id.movie_name)).setText(name);
        ((TextView) findViewById(R.id.movie_description)).setText(description);
        ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(MovieBackend.getPoster(posterURL));
        ((TextView) findViewById(R.id.movie_genre)).setText(genres);
        ((TextView) findViewById(R.id.movie_genre)).bringToFront();
        ((TextView) findViewById(R.id.movie_release)).setText(releaseDate);
        ((TextView) findViewById(R.id.movie_runtime)).setText(runtime);
        ((TextView) findViewById(R.id.movie_director)).setText(director);
        ((TextView) findViewById(R.id.movie_rating)).setText(rating + " / 10");
        ((TextView) findViewById(R.id.movie_actors)).setText(actors);
    }

    // TODO IMPLEMENT LATER ONCE WE HAVE FUNCTIONALITY
    protected void watchlistOnButtonPressed(View view) {
        Toast.makeText(MovieDetailsActivity.this, "Watchlist Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    // TODO IMPLEMENT LATER ONCE WE HAVE FUNCTIONALITY
    protected void reviewOnButtonPressed(View view) {
        Toast.makeText(MovieDetailsActivity.this, "Reviews Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    private String getActorString(String actors) {
        StringBuilder builder = new StringBuilder();
        int firstComma = actors.indexOf(',');
        if (firstComma == -1) {
            return actors;
        }
        builder.append(actors.substring(0, firstComma));
        int secondComma = actors.indexOf(',', firstComma + 1);
        if (secondComma == -1) {
            return builder.toString();
        }
        builder.append("\n");
        builder.append(actors.substring(firstComma + 2, secondComma));
        return builder.toString();
    }

    private String getDirectorString(String directors) {
        StringBuilder builder = new StringBuilder();
        int firstComma = directors.indexOf(',');
        if (firstComma == -1) {
            return directors;
        }
        builder.append(directors.substring(0, firstComma));
        return builder.toString();
    }

}