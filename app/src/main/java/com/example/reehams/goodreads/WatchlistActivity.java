package com.example.reehams.goodreads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.reehams.goodreads.WelcomeActivity.userId1;
import static com.example.reehams.goodreads.WelcomeActivity.watchlist;

public class WatchlistActivity extends AppCompatActivity {

    /*ListView lv;
    ArrayAdapter<String> adapter;
    DatabaseReference db;
    FireBaseHelper help;*/

    private DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        Bundle extras = getIntent().getExtras();
        //String userId = extras.getString("user_id");
        String fbUserId = userId1;
        String movieId = extras.getString("movie_id");
        TextView textView = (TextView) findViewById(R.id.textView6);
        textView.setText("UserID: " + userId1 + "\nMovieID: " + movieId);


        myDatabase = FirebaseDatabase.getInstance().getReference();

        Movie movie = new Movie(movieId);
        WelcomeActivity.watchlist.add(movie);

        for (Movie currentMovie : watchlist) {
            myDatabase.child(userId1).child("watchlist").setValue(currentMovie);
        }





        String id = movie.getMovie();



        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.Lv);
        db = FirebaseDatabase.getInstance().getReference().child(db.push().getKey());
        help = new FireBaseHelper(db);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, help.retrieve());
        lv.setAdapter(adapter);*/

    }
}
