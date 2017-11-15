package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import edu.upenn.goodwatch.BackgroundTasks.WatchlistItem;
import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;
import edu.upenn.goodwatch.LayoutClasses.WatchlistArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WatchlistActivity extends SideBar {
    private final String DEBUG_TAG = getClass().getSimpleName();
    DatabaseReference reference;
    private ListView mListView;
    String[] searchResults; // Options to be shown in list view
    private ArrayList<WatchlistItem> myMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.watchlistView);
        final WatchlistArrayAdapter arrayAdapter = new WatchlistArrayAdapter(this, android.R.layout.simple_list_item_1, myMovies);
        mListView.setAdapter(arrayAdapter);
        myMovies.clear();
        myMovies.add(new WatchlistItem("Loading..."));
        arrayAdapter.notifyDataSetChanged();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(userId).child("watchlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isEmpty = false;
                Set<WatchlistItem> newWatchlistItems = new HashSet<>();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String watchlistEntry = childSnapshot.getValue(String.class);
                    if (watchlistEntry.equals("null")) {
                        isEmpty = true;
                        break;
                    }
                    String[] elts = watchlistEntry.split(",");
                    final WatchlistItem i = new WatchlistItem(elts[1]);
                    newWatchlistItems.add(i);
                    String movieId = elts[0];
                    // Get the average user rating (if one exists) for every movie in watchlist
                    reference.child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String avg = getAverageRating(dataSnapshot);
                            i.setAverageRating(avg);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                myMovies.clear();
                myMovies.addAll(newWatchlistItems);
                arrayAdapter.notifyDataSetChanged();
                if (isEmpty) {
                    searchResults = new String[1];
                    searchResults[0] = "empty";
                }
                else {
                    searchResults = new String[myMovies.size()];
                    for (WatchlistItem movie : myMovies) {
                        try {
                            String movieId = search(movie.getMovieTitle().replace(" ", "%20")).getJSONObject(0).get("id").toString();
                            searchResults[myMovies.indexOf(movie)] = movieId;
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "watchlist.exception"), e);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(WatchlistActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
                // Do nothing if there is no result
                if (searchResults[position] == null) {
                    Toast.makeText(WatchlistActivity.this, "Null searchresult ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (searchResults[position].equals("empty")) {
                    return;
                }
                if (searchResults[position].equals("") || searchResults[position].equals("No Results Found")) {
                    Toast.makeText(WatchlistActivity.this, "Empty ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Pass the data of the clicked movie to the movieDetails class
                Intent i = new Intent(WatchlistActivity.this,  MovieDetailsActivity.class);
                i.putExtra("user_id", userId);
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
                        Toast.makeText(WatchlistActivity.this, "More movie details cannot be found in IMBD Database",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (imbdId.charAt(imbdId.length() - 1) == '/') {
                        imbdId = imbdId.substring(0, imbdId.length() - 1);
                    }
                    i.putExtra("JSON_Data", imbdId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(i);
            }
        });


    }

    private String getAverageRating(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null || dataSnapshot.getValue() == null) {
            return "Be the first to review!";
        }
        double total = 0;
        double numReviews = 0;
        for (DataSnapshot reviewEntry : dataSnapshot.getChildren()) {
            Review r = reviewEntry.getValue(Review.class);
            total += Integer.parseInt(r.getRating());
            numReviews += 1;
        }
        double avg = (numReviews == 0) ? 0 : total / numReviews;
        return String.format("Users rated this movie %.2f/10 on average", avg);
    }

    public JSONArray search(String query) {
        // Set up query
        String[] queryArr = new String[1];
        queryArr[0] = Config.getSearchUrl(getBaseContext(), query);
        AsyncTask search = new MovieBackend().execute(queryArr);
        JSONObject json = null;
        // Search and add
        try {
            // Get search results as a JSONArray
            json = (JSONObject) search.get();
            JSONArray resultsArr = json.getJSONArray("results");
            return resultsArr;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(WatchlistActivity.this, Messages.getMessage(getBaseContext(), "watchlist.searchError"),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
