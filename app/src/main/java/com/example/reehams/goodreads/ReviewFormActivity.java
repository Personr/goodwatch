package com.example.reehams.goodreads;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.*;
import com.google.firebase.database.*;

public class ReviewFormActivity extends SideBar implements AdapterView.OnItemSelectedListener {


    private String[] ratingSpinner;
    private String rating;
    private EditText review;
    private Button submitBtn;
    private Button cancelBtn;
    private String reviewText;
    private String movieName;
    private TextView reviewHeader;
    private DatabaseReference myDatabase;
    private String movieId;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);
        super.onCreateDrawer();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        reviewHeader = (TextView) findViewById(R.id.reviewheader);
        movieName = getIntent().getStringExtra("movie_name");
        movieId = getIntent().getStringExtra("movie_id");
        userId = getIntent().getStringExtra("user_id");
        reviewHeader.setText("Review " + movieName + " below!");

        review = (EditText) findViewById(R.id.reviewEditText);

        // Saving user review text from form to Firebase
        submitBtn = (Button) findViewById(R.id.button2);
        cancelBtn = (Button) findViewById(R.id.button3);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ReviewFormActivity.this);

                // set title
                alertDialogBuilder.setTitle("Submit Review");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to submit this review?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                reviewText = review.getText().toString();
                                String name = WelcomeActivity.facebookName;
                                String userId = WelcomeActivity.userId1;
                                final Review review1 = new Review(movieId, rating, reviewText);
                                // TODO THING WITH LISTS
                                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // Get user value
                                                //List l = dataSnapshot.getValue(List.class);

                                                List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                                                if (l.get(0).equals("null")) {
                                                    l.remove(0);
                                                }
                                                l.add(review1.toString());
                                                myDatabase.child(ReviewFormActivity.this.userId).child("reviews").setValue(l);

                                                //user.email now has your email value
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                Intent i = new Intent(ReviewFormActivity.this,MovieDetailsActivity.class);
                                Bundle extras = new Bundle();
                                extras.putString("user_id", userId);
                                extras.putString("JSON_Data", movieId);
                                i.putExtras(extras);
                                startActivity(i);
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
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ReviewFormActivity.this);

                // set title
                alertDialogBuilder.setTitle("Cancel Review");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure you want to cancel your review?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                Intent i = new Intent(ReviewFormActivity.this, MovieDetailsActivity.class);
                                Bundle extras = new Bundle();
                                extras.putString("user_id", userId);
                                extras.putString("JSON_Data", movieId);
                                i.putExtras(extras);
                                startActivity(i);
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
        });


        this.ratingSpinner = new String[] {"0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        switch(pos) {
            case 0:
                rating = "0.0";
                break;
            case 1:
                rating = "0.5";
                break;
            case 2:
                rating = "1.0";
                break;
            case 3:
                rating = "1.5";
                break;
            case 4:
                rating = "2.0";
                break;
            case 5:
                rating = "2.5";
                break;
            case 6:
                rating = "3.0";
                break;
            case 7:
                rating = "3.5";
                break;
            case 8:
                rating = "4.0";
                break;
            case 9:
                rating = "4.5";
                break;
            case 10:
                rating = "5.0";
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another Interface callback
    }


}
