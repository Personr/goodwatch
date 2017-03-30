package com.example.reehams.goodreads;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import static com.example.reehams.goodreads.WelcomeActivity.facebookName;
import static com.example.reehams.goodreads.WelcomeActivity.userId1;

public class WatchlistActivity extends SideBar {
    DatabaseReference reference;
    private ListView mListView;
    String[] searchResults; // Options to be shown in list view
    private ArrayList<String> myMovies = new ArrayList<>();
    String userId; // retrieves user's unique Id from previous intent for use

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.watchlistView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myMovies);
        mListView.setAdapter(arrayAdapter);
        userId = getIntent().getStringExtra("user_id");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    //set.add(childSnapshot.getKey());
                    if(childSnapshot.child("id").getValue(String.class).equals(userId1)) {
                        if (!childSnapshot.child("name").getValue(String.class).equals(facebookName)) {
                            set.add(childSnapshot.child("name").getValue(String.class));
                        }
                    }
                }
                myMovies.clear();
                myMovies.addAll(set);
                arrayAdapter.notifyDataSetChanged();



                searchResults = new String[myMovies.size()];
                int i = 1;
                for (String movie : myMovies) {
                    try {
                        //Toast.makeText(WatchlistActivity.this, "/" + movie + "/ Index: " + myMovies.indexOf(movie), Toast.LENGTH_SHORT).show();
                        String movieId = search(movie.replace(" ", "%20")).getJSONObject(0).get("id").toString();
                        searchResults[myMovies.indexOf(movie)] = movieId;
                        //Toast.makeText(WatchlistActivity.this, movie + " Index: " + myMovies.indexOf(movie), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(WatchlistActivity.this, movie + "Search Results value: " + movieId, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(WatchlistActivity.this, "Movies shown: " + i, Toast.LENGTH_SHORT).show();
                        i++;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
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
                    queryArr[0] = "https://api.themoviedb.org/3/movie/" + movieId +
                            "?api_key=9f4d052245dda68f14bcbd986787dc7b&language=en-US";
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

    public JSONArray search(String query) {
        // Set up query
        String[] queryArr = new String[1];
        queryArr[0] = "https://api.themoviedb.org/3/search/movie?api_key=9f4d052245dda68f14bcbd986787dc7b&language=en-US&query="
                + query
                +"&page=1&include_adult=false";
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
            Toast.makeText(WatchlistActivity.this, "Search error",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
