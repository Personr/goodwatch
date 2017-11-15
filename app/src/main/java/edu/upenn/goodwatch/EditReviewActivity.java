package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.upenn.goodwatch.FileAccess.Messages;

public class EditReviewActivity extends AppCompatActivity {
    private String currentReview = "";
    private String rating = "";
    private String userId;
    private String userName;
    private String email;
    private String movieId;
    private String targetTimeStamp;
    private final String DEBUG_TAG = this.getClass().getName();
    private DatabaseReference myDatabase;
    private  String accountID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDatabase = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Retrieve passed parameters

        final String movieName = getIntent().getStringExtra("movie_name");
        movieId = getIntent().getStringExtra("movie_id");
        targetTimeStamp = getIntent().getStringExtra("targetTime");
        userId = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        String initialReview = getIntent().getStringExtra("review");
        int initialRating = Integer.parseInt(getIntent().getStringExtra("rating"));
        accountID = getIntent().getStringExtra("accId");

        //Get references to widgets
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerEditReview);
        final TextView editedReview = (TextView) findViewById(R.id.newReview);
        Button submitEditButton = (Button) findViewById(R.id.submitEdittedReview);
        Button deleteButton = (Button) findViewById(R.id.deleteReview);


        // Create an ArrayAdapter using the string array and a default spinner layout
        String[] ratingSpinner = new String[]{Messages.getMessage(getBaseContext(), "review.selectRating"),
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        //Initialize the screen state to actual reviews state
        rating = initialRating + 1 + "";
        currentReview = initialReview;
        spinner.setSelection(initialRating + 1);
        editedReview.setText((CharSequence) initialReview);

        //Listener for the text view
        editedReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                currentReview = editedReview.getText().toString();
            }
        });

        //Listener for submitting
        submitEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
                rating = spinner.getSelectedItem().toString();
                Review editedReview = new Review(movieId, rating, currentReview, movieName);
                //Add the new review to the database
                ReviewFormValueEventListener listener = new ReviewFormValueEventListener(editedReview, myDatabase, userId, targetTimeStamp);
                ReviewFormValueMovieEventListener movieEventListener = new ReviewFormValueMovieEventListener(myDatabase, movieId, editedReview);
                myDatabase.addListenerForSingleValueEvent(movieEventListener);

                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(listener);
                Intent i = new Intent(EditReviewActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        //Listener for deleting
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit();
                rating = spinner.getSelectedItem().toString();
                Review editedReview = new Review(movieId, rating, currentReview, movieName);
                editedReview.time = targetTimeStamp;

                ReviewFormValueEventListener listener = new ReviewFormValueEventListener(editedReview, myDatabase, userId, targetTimeStamp);
                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(listener);
                Intent i = new Intent(EditReviewActivity.this, HomeActivity.class);
                startActivity(i);

            }
        });

    }

    public void edit() {

        //Remove from movie lists as well
        myDatabase.child(movieId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren() ) {
                            HashMap<String, String> map = (HashMap<String, String>)  appleSnapshot.getValue();
                            if (map == null) continue;
                            if (map.get("time").equals(targetTimeStamp) && map.get("movieId").equals(movieId)) {
                                appleSnapshot.getRef().setValue(null);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

}
