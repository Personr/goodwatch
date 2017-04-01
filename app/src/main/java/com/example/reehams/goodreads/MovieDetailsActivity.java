package com.example.reehams.goodreads;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.reehams.goodreads.WelcomeActivity.facebookName;
import static com.example.reehams.goodreads.WelcomeActivity.userId1;

/**
 * Created by rahulkooverjee on 3/9/17.
 */

public class MovieDetailsActivity extends SideBar {

    String userId;
    String movieId;
    String nameId;

    String movieName;

    private DatabaseReference myDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity);
        super.onCreateDrawer();
        myDatabase = FirebaseDatabase.getInstance().getReference();
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
            movieId = id;
            userId = getIntent().getStringExtra("user_id");
            // Get movie details in JSON object
            String[] queryArr = new String[1];
            queryArr[0] = "http://www.omdbapi.com/?i=" + id;
            AsyncTask search = new MovieBackend().execute(queryArr);
            JSONObject json = null;
            json = (JSONObject) search.get();
            // Set movie info
            name = json.get("Title").toString();
            movieName = name;
            nameId = name;
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
        myDatabase.child(userId).child("watchlist").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                        Button watchButton = (Button) findViewById(R.id.watchlist_button);
                        if (l.contains(movieId + "," + movieName)) {
                            watchButton.setText("Remove from Watchlist");
                        }
                        else {
                            watchButton.setText("Add to Watchlist");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    // TODO IMPLEMENT LATER ONCE WE HAVE FUNCTIONALITY
    protected void watchlistOnButtonPressed(View view) {
        myDatabase.child(userId).child("watchlist").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                        Button watchButton = (Button) findViewById(R.id.watchlist_button);
                        if (l.contains(movieId + "," + movieName)) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        MovieDetailsActivity.this);

                                // set title
                                alertDialogBuilder.setTitle("Remove from Watchlist");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Are you sure you want to remove the movie from your watchlist?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                String s = movieId + "," + movieName;
                                                l.remove(s);
                                                if (l.isEmpty()) {
                                                    l.add("null");
                                                }
                                                myDatabase.child(userId).child("watchlist").setValue(l);
                                                Intent i = new Intent(MovieDetailsActivity.this,WatchlistActivity.class);
                                                Bundle extras = new Bundle();
                                                extras.putString("user_id", userId);
                                                extras.putString("movie_id", movieId);
                                                i.putExtras(extras);
                                            }
                                        })
                                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                dialog.cancel();
                                            }
                                        });
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                        }
                        else {
                            myDatabase.child(userId).child("watchlist").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                                            if (l.get(0).equals("null")) {
                                                l.remove(0);
                                            }
                                            String s = movieId + "," + movieName;
                                            if (!l.contains(s)) {
                                                l.add(s);
                                            }
                                            myDatabase.child(userId).child("watchlist").setValue(l);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            Intent i = new Intent(MovieDetailsActivity.this,WatchlistActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("user_id", userId);
                            extras.putString("movie_id", movieId);
                            i.putExtras(extras);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }



    // TODO IMPLEMENT LATER ONCE WE HAVE FUNCTIONALITY
    protected void reviewOnButtonPressed(View view) {
        Intent i = new Intent(MovieDetailsActivity.this, ReviewFormActivity.class);
        Bundle extras = new Bundle();
        extras.putString("user_id", userId);
        extras.putString("movie_id", movieId);
        extras.putString("movie_name", movieName);
        i.putExtras(extras);
        startActivity(i);
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