package com.example.reehams.goodreads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.reehams.goodreads.WelcomeActivity.userId1;

public class WatchlistActivity extends AppCompatActivity {
    private DatabaseReference myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

    }
}
