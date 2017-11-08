package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.upenn.goodwatch.BackgroundTasks.WatchlistNotificationService;
import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;
import edu.upenn.goodwatch.LayoutClasses.ReviewListAdapter;
import edu.upenn.goodwatch.Listeners.ReviewsListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.*;

import org.json.JSONObject;

import java.util.*;


/**
 * Created by rahulkooverjee on 3/30/17.
 */

public class HomeActivity extends SideBar {

    private final String DEBUG_TAG = getClass().getSimpleName();

    private ArrayList<Review> reviewList = new ArrayList<>();
    private HomeActivity activity = this;
    private Map<String, Set<Review>> usersReviews = new HashMap<>();
    private ArrayAdapter arrayAdapter;
    private DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();

    private ListView reviewListView;
    private TextView noEvalView;
    private TextView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        super.onCreateDrawer();

        reviewListView = (ListView) findViewById(R.id.yourReviewList);
        noEvalView = (TextView) findViewById(R.id.noEvalView);
        loadingView = (TextView) findViewById(R.id.loadingView);

        noEvalView.setText(Messages.getMessage(getBaseContext(), "home.noReview"));
        loadingView.setText(Messages.getMessage(getBaseContext(), "follow.loading"));
        reviewListView.setVisibility(View.INVISIBLE);
        noEvalView.setVisibility(View.INVISIBLE);
        loadingView.setVisibility((View.VISIBLE));

        arrayAdapter = new ReviewListAdapter(this, reviewList);
        reviewListView.setAdapter(arrayAdapter);
        reviewList.clear();
        reviewList.add(new Review("Loading..."));
        arrayAdapter.notifyDataSetChanged();
        waitForFirebase();
        startAllServices();
    }

    public void helper(long delay) {
        reviewListView = (ListView) findViewById(R.id.yourReviewList);
        final ArrayAdapter arrayAdapter2 = new ReviewListAdapter(this, reviewList);
        reviewListView.setAdapter(arrayAdapter2);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myDatabase.child(userId).child("followingIds").addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                                for (String s : l) {
                                    if (s==null) break;
                                    if (s.equals("null")) break;
                                    final String id = s;
                                    myDatabase.child(id).child("reviews").addValueEventListener(
                                            new ReviewsListener(myDatabase, id, getUserReviews(id), activity));
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        }, delay); // wait a while basically
        displaySet();
    }

    private Set<Review> getUserReviews(String id) {
        Set<Review> userReviews = usersReviews.get(id);
        if (userReviews == null) {
            userReviews = new TreeSet<>();
            usersReviews.put(id, userReviews);
        }
        return userReviews;
    }

    private boolean isUsersReviewsEmpty() {
        for (Map.Entry<String, Set<Review>> entry : usersReviews.entrySet())
        {
            if (!entry.getValue().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private int usersReviewsLength() {
        int length = 0;
        for (Map.Entry<String, Set<Review>> entry : usersReviews.entrySet())
        {
            length += entry.getValue().size();
        }

        return length;
    }

    public void refresh() {
        arrayAdapter.notifyDataSetChanged();
    }

    public void displaySet() {
        arrayAdapter = new ReviewListAdapter(this, reviewList);
        reviewListView.setAdapter(arrayAdapter);
        reviewList.clear();

        if (isUsersReviewsEmpty()) {
            reviewListView.setVisibility(View.INVISIBLE);
            noEvalView.setVisibility(View.VISIBLE);
            loadingView.setVisibility((View.INVISIBLE));
        }
        else {
            reviewListView.setVisibility(View.VISIBLE);
            noEvalView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility((View.INVISIBLE));

            for (Map.Entry<String, Set<Review>> entry : usersReviews.entrySet())
            {
                for (Review rev : entry.getValue()) {
                    reviewList.add(rev);
                }
            }
            Collections.sort(reviewList);
        }
        refresh();
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(WatchlistActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
                // Do nothing if there is no result
                if (reviewList.get(position) == null) {
                    Toast.makeText(HomeActivity.this, "Null searchresult ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Pass the data of the clicked movie to the movieDetails class
                Intent i = new Intent(HomeActivity.this,  MovieDetailsActivity.class);
                i.putExtra("user_id", userId);
                try {
                    // Pass the IMBD movie id to the details page
                    String movieId = reviewList.get(position).getMovieId();
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
                        Toast.makeText(HomeActivity.this, "More movie details cannot be found in IMBD Database",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (imbdId.charAt(imbdId.length() - 1) == '/') {
                        imbdId = imbdId.substring(0, imbdId.length() - 1);
                    }
                    i.putExtra("JSON_Data", imbdId);
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "Exception arises when querying moviedb API by IMDB ID\n", e);
                }
                startActivity(i);
            }
        });
    }

    private void waitForFirebase() {
        myDatabase.child(userId).child("followingIds").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                        if (l == null) {
                            waitForFirebase();
                        }
                        else {
                            helper(0);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void startAllServices() {
        Intent watchlistNotificationIntent = new Intent(this, WatchlistNotificationService.class);
        watchlistNotificationIntent.putExtra("UserID", userId);
        startService(watchlistNotificationIntent);
    }


}

