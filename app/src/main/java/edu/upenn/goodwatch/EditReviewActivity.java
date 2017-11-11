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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.upenn.goodwatch.FileAccess.Messages;

public class EditReviewActivity extends AppCompatActivity {
    private String currentReview = "";
    private String rating = "";
    private String userId;
    private String userName;
    private String email;
    private final String DEBUG_TAG = this.getClass().getName();
    private DatabaseReference myDatabase;

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
        final String movieId = getIntent().getStringExtra("movie_id");
        userId = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        String initialReview = getIntent().getStringExtra("review");
        int initialRating = Integer.parseInt(getIntent().getStringExtra("rating"));

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
                rating = spinner.getSelectedItem().toString();
                Review editedReview = new Review(movieId, rating, currentReview, movieName);
                //Add the new review to the database
                ReviewFormValueEventListener listener = new ReviewFormValueEventListener(editedReview, myDatabase, userId);
                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(listener);
                goBack();
            }
        });

        //Listener for deleting
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

    }

    public void goBack() {
        Intent i = new Intent(EditReviewActivity.this, AccountInformationActivity.class);
        i.putExtra("id", userId);
        i.putExtra("name", userName);
        i.putExtra("email", email);
        startActivity(i);
    }

}
