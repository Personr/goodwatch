package edu.upenn.goodwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import edu.upenn.goodwatch.FileAccess.Messages;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        reviewHeader.setText(Messages.reviewBelow(getBaseContext(), movieName));

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
                alertDialogBuilder.setTitle(Messages.getMessage(getBaseContext(), "review.submit"));

                if (rating.equals("blank")) {
                    Toast.makeText(getApplicationContext(), Messages.getMessage(getBaseContext(), "review.select"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // set dialog message
                alertDialogBuilder
                        .setMessage(Messages.getMessage(getBaseContext(), "review.confirmSubmit"))
                        .setCancelable(false)
                        .setPositiveButton(Messages.getMessage(getBaseContext(), "follow.yes"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                reviewText = review.getText().toString();
                                final Review review1 = new Review(movieId, rating, reviewText, movieName);
                                ReviewFormValueEventListener listener = new ReviewFormValueEventListener(review1,
                                        myDatabase, ReviewFormActivity.this.userId);
                                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(listener);
                                boolean b = myDatabase.child(movieId).getDatabase() != null;

                                ReviewFormValueMovieEventListener listenerMovie = new ReviewFormValueMovieEventListener(myDatabase, movieId, review1);
                                myDatabase.addListenerForSingleValueEvent(listenerMovie);

                                Intent i = new Intent(ReviewFormActivity.this, MovieDetailsActivity.class);
                                Bundle extras = new Bundle();
                                extras.putString("user_id", userId);
                                extras.putString("JSON_Data", movieId);
                                i.putExtras(extras);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(Messages.getMessage(getBaseContext(), "follow.no"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                alertDialogBuilder.setTitle(Messages.getMessage(getBaseContext(), "review.cancel"));

                // set dialog message
                alertDialogBuilder
                        .setMessage(Messages.getMessage(getBaseContext(), "review.confirmCancel"))
                        .setCancelable(false)
                        .setPositiveButton(Messages.getMessage(getBaseContext(), "follow.yes"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                        .setNegativeButton(Messages.getMessage(getBaseContext(), "follow.no"),
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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


        this.ratingSpinner = new String[]{Messages.getMessage(getBaseContext(), "review.selectRating"),
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        if (pos == 0) {
            rating = "blank";
        } else if (1 <= pos && pos <= 11) {
            rating = Integer.toString(pos - 1);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another Interface callback
    }

    @Override
    protected void onCreateDrawer() {
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        userId = getIntent().getStringExtra("user_id");

        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

   @Override
   protected void addDrawerItems() {
       String[] osArray = { Messages.getMessage(getBaseContext(), "review.home"),
               Messages.getMessage(getBaseContext(), "review.myAccount"),
               Messages.getMessage(getBaseContext(), "review.myWatchlist"),
               Messages.getMessage(getBaseContext(), "review.topCharts"),
               Messages.getMessage(getBaseContext(), "review.movieSearch"),
               Messages.getMessage(getBaseContext(), "review.userSearch"),
               Messages.getMessage(getBaseContext(), "review.aboutUs"),
               Messages.getMessage(getBaseContext(), "review.logOut")};
       mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
       mDrawerList.setAdapter(mAdapter);
       mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       ReviewFormActivity.this);

               // set title
               alertDialogBuilder.setTitle(Messages.getMessage(getBaseContext(), "review.cancel"));

               // set dialog message
               alertDialogBuilder
                       .setMessage(Messages.getMessage(getBaseContext(), "review.confirmNavigate"))
                       .setCancelable(false)
                       .setPositiveButton(Messages.getMessage(getBaseContext(), "follow.yes"), new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               Class targetActivity = null;
                               if (position == 0) {
                                   targetActivity = HomeActivity.class;
                               }
                               if (position == 1) {
                                   targetActivity = AccountInformationActivity.class;
                               }
                               if (position == 2) {
                                   targetActivity = WatchlistActivity.class;
                               }
                               if (position == 3) {
                                   targetActivity = TopChartsActivity.class;
                               }
                               if (position == 4) {
                                   targetActivity = MovieActivity.class;
                               }
                               if (position == 5) {
                                   targetActivity = UserSearch.class;
                               }
                               if (position == 6) {
                                   targetActivity = AboutUs.class;
                               }
                               if (position == 7) {
                                   targetActivity = LogOutActivity.class;
                               }
                               Intent i = new Intent(ReviewFormActivity.this, targetActivity);
                               i.putExtra("user_id", userId);
                               startActivity(i);
                           }
                       })
                       .setNegativeButton(Messages.getMessage(getBaseContext(), "follow.no"), new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               // if this button is clicked, just close
                               // the dialog box and close drawer too
                               dialog.cancel();
                               mDrawerLayout.closeDrawer(Gravity.LEFT);
                           }
                       });
               // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();

               // show it
               alertDialog.show();
           }
       });
   }
}
