package edu.upenn.goodwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;

import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by reehams on 3/26/17.
 */

public class AccountInformationActivity extends SideBar {
    TextView userEmailView;
    TextView userNameView;
    TextView userBioView;
    Button followButton;
    Button editButton;
    ListView userReviewsView;
    ImageView imageView;

    String[] searchResults;
    private ArrayList<String> userReviewsList = new ArrayList<>();
    final Set<Review> set = new TreeSet<Review>();

    private String accountID;
    private String accountName;
    private String accountEmail;
    private String accountBio;
    private String accountImage;

    private final String DEBUG_TAG = getClass().getSimpleName();
    private DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_information_activity);
        setTitle("Account");
        super.onCreateDrawer();
        FacebookSdk.sdkInitialize(getApplicationContext());

        myDatabase = FirebaseDatabase.getInstance().getReference();
        accountID = getIntent().getStringExtra("id");

        userEmailView = (TextView) findViewById(R.id.email);
        userNameView = (TextView) findViewById(R.id.userName);
        userBioView = (TextView) findViewById(R.id.userBio);

        imageView = (ImageView) findViewById(R.id.image);

        followButton = (Button) findViewById(R.id.followbotton);
        editButton = (Button) findViewById(R.id.editButton);

        userReviewsView = (ListView) findViewById(R.id.userReviewsList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userReviewsList);
        userReviewsView.setAdapter(arrayAdapter);
        userReviewsList.clear();
        userReviewsList.add(Messages.getMessage(getBaseContext(), "follow.loading"));
        arrayAdapter.notifyDataSetChanged();

        //setup user information listener
        myDatabase.child(accountID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name and email
                accountName = (String) dataSnapshot.child("name").getValue();
                userNameView.setText(accountName);
                accountEmail = (String) dataSnapshot.child("email").getValue();
                userEmailView.setText(accountEmail);

                //get bio if it exists
                accountBio = (String) dataSnapshot.child("bio").getValue();
                if(accountBio == null) {
                    userBioView.setText(Messages.noBio(getBaseContext(), accountName));
                }
                else {
                    userBioView.setText(accountBio);
                }

                //get image if it exists
                accountImage = (String) dataSnapshot.child("photoUrl").getValue();
                if(accountImage == null) {
                    //image.setImageBitmap()
                }
                else {
                    new DownloadImageTask(imageView).execute(accountImage);
                }


                //get reviews, if any
                List<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) dataSnapshot.child("reviews").getValue();
                for (HashMap<String, String> s : l) {
                    if (s == null ) continue;
                    String movieId = s.get("movieId");
                    if (movieId.equals("null")) continue;
                    String movieTitle = s.get("movieTitle");
                    String rating = s.get("rating");
                    String reviewText = s.get("reviewText");
                    if (reviewText.length() > 175) {
                        reviewText = reviewText.substring(0, 175) + "...";
                    }
                    String time = s.get("time");
                    Review r = new Review(movieId, rating, reviewText, movieTitle, time);
                    set.add(r);
                }
                userReviewsList.clear();
                if (set.isEmpty()) {
                    searchResults = new String[1];
                    searchResults[0] = "empty";
                    userReviewsList.add(Messages.noReview(getBaseContext(), accountName));
                }
                else {
                    searchResults = new String[set.size()];
                    int i = 0;
                    for (Review rev : set) {
                        searchResults[i] = rev.movieId;
                        String displayText = rev.movieTitle + "\n" + rev.getStars() + "\n\"" + rev.reviewText + "\"";
                        userReviewsList.add(displayText);
                        i++;
                    }
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //if this is the current user's profile, disable follow button.
        if (userId.equals(accountID)) {
            followButton.setVisibility(View.INVISIBLE);
        //otherwise, disable the edit profile button and make the follow button either +follow or -unfollow.
        } else {
            editButton.setVisibility(View.INVISIBLE);
            myDatabase.child(userId).child("followingIds").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    followButton.setText(Messages.getMessage(getBaseContext(), "follow.follow"));
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.getValue(String.class).equals(accountID)) {
                            followButton.setText(Messages.getMessage(getBaseContext(), "follow.unfollow"));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        myDatabase.child(accountID).child("reviews").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        userReviewsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.v(DEBUG_TAG, "LONG PRESS RECEIVED");
                if (searchResults[position] == null) {
                    Toast.makeText(AccountInformationActivity.this,
                            Messages.getMessage(getBaseContext(), "follow.nullId"),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (searchResults[position].equals("empty")) {
                    return false;
                }


                //Redirect traffic to review activity to get input
                Intent i = new Intent(AccountInformationActivity.this, EditReviewActivity.class);
                Bundle extras = new Bundle();
                String movieId = searchResults[position];
                extras.putString("user_id", userId);
                String movieName = "";
                String currentReview = "";
                String currentRating = "";
                String timeStamp = "";

                //Find the movie with the id
                for (Review item : set) {
                    String currentMovieID = item.movieId;
                    if (currentMovieID.equals(movieId)) {
                        movieName = item.movieTitle;
                        currentReview = item.reviewText;
                        currentRating =  item.rating;
                        timeStamp = item.time;
                        //item.
                    }
                }

                final String identfier = timeStamp;
                myDatabase.child(userId).child("reviews").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren() ) {
                                    HashMap<String, String> map = (HashMap<String, String>)  appleSnapshot.getValue();
                                    if (map == null) continue;
                                    if (map.get("time").equals(identfier)) {
                                            appleSnapshot.getRef().removeValue();
                                        }
                                    }


                                }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                            }
                        });

                //Pass desired parameters
                extras.putString("movie_id", movieId);
                extras.putString("movie_name", movieName);
                extras.putString("rating", currentRating);
                extras.putString("review", currentReview);
                extras.putString("email", accountEmail);
                extras.putString("username", userName);

                i.putExtras(extras);
                startActivity(i);

                return true;

            }
        });

        userReviewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(WatchlistActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
                // Do nothing if there is no result
                if (searchResults[position] == null) {
                    Toast.makeText(AccountInformationActivity.this,
                            Messages.getMessage(getBaseContext(), "follow.nullId"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (searchResults[position].equals("empty")) {
                    return;
                }
                // Pass the data of the clicked movie to the movieDetails class
                Intent i = new Intent(AccountInformationActivity.this,  MovieDetailsActivity.class);
                i.putExtra("user_id", accountID);
                try {
                    // Pass the IMBD movie id to the details page
                    String movieId = searchResults[position];
                    String[] queryArr = new String[1];
                    queryArr[0] = Config.getMovieInfoUrl(getBaseContext(), movieId);
                    AsyncTask search = new MovieBackend().execute(queryArr);
                    JSONObject json = null;
                    json = (JSONObject) search.get();
                    String imbdId = json.get("imdb_id").toString();
                    boolean isInvalid = (imbdId == null);
                    if (!isInvalid) {
                        isInvalid = imbdId.equals("") || imbdId.equals("null");
                    }
                    if (isInvalid) {
                        Toast.makeText(AccountInformationActivity.this,
                                Messages.getMessage(getBaseContext(), "follow.detailsNotFound"),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (imbdId.charAt(imbdId.length() - 1) == '/') {
                        imbdId = imbdId.substring(0, imbdId.length() - 1);
                    }
                    i.putExtra("JSON_Data", imbdId);
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "follow.imdbException"), e);
                }
                startActivity(i);
            }
        });
    }

    protected void followThisUser(View view) {
        // Add the person you followed to your list of followers
        myDatabase.child(userId).child("followingIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isFollowing = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getValue(String.class).equals(accountID)) {
                        isFollowing = true;
                        break;
                    }
                }

                final List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                if (isFollowing) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            AccountInformationActivity.this);

                    // set title
                    alertDialogBuilder.setTitle(Messages.getMessage(getBaseContext(), "follow.removeFollowing"));
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(Messages.getMessage(getBaseContext(), "follow.confirmUnfollow"))
                            .setCancelable(false)
                            .setPositiveButton(Messages.getMessage(getBaseContext(), "follow.yes")
                                    , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    String s = accountID;
                                    l.remove(s);
                                    if (l.isEmpty()) {
                                        l.add("null");
                                    }
                                    followButton.setText(Messages.getMessage(getBaseContext(), "follow.follow"));
                                    myDatabase.child(userId).child("followingIds").setValue(l);
                                }
                            })
                            .setNegativeButton(Messages.getMessage(getBaseContext(), "follow.no"),
                                    new DialogInterface.OnClickListener() {
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
                    if (l.get(0).equals("null")) {
                        l.remove(0);
                    }
                    String s = accountID;
                    if (!l.contains(s)) {
                        l.add(s);
                    }
                    followButton.setText(Messages.getMessage(getBaseContext(), "follow.unfollow"));
                    myDatabase.child(userId).child("followingIds").setValue(l);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Add yourself as a follower to the person you just followed
        myDatabase.child(accountID).child("followerIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isFollowing = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getValue(String.class).equals(userId)) {
                        isFollowing = true;
                    }
                }
                final List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                if (isFollowing) {
                    String s = userId;
                    l.remove(s);
                    if (l.isEmpty()) {
                        l.add("null");
                    }
                    myDatabase.child(accountID).child("followerIds").setValue(l);
                }
                else {
                   if (l.get(0).equals("null")) {
                        l.remove(0);
                    }
                    String s = userId;
                    if (!l.contains(s)) {
                        l.add(s);
                    }
                    myDatabase.child(accountID).child("followerIds").setValue(l);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    protected void editProfile(View view) {
        Intent i = new Intent(AccountInformationActivity.this, EditProfileActivity.class);
        i.putExtra("id", accountID);
        i.putExtra("name", accountName);
        i.putExtra("email", accountEmail);
        i.putExtra("bio", accountBio);
        i.putExtra("photoUrl", accountImage);
        startActivity(i);
    }

    protected void followingOfTheUser(View view) {
        Intent i = new Intent(AccountInformationActivity.this, UserListActivity.class);
        i.putExtra("id", accountID);
        i.putExtra("dataName", "followingIds");
        i.putExtra("errorID", "followerList.notFollowing");
        startActivity(i);
    }

    protected void followersOfTheUser(View view) {
        Intent i = new Intent(AccountInformationActivity.this, UserListActivity.class);
        i.putExtra("id", accountID);
        i.putExtra("dataName", "followerIds");
        i.putExtra("errorID", "followerList.noFollowers");
        startActivity(i);
    }
}
