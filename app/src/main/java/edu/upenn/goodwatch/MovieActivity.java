package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by rahulkooverjee on 2/20/17.
 */

public class MovieActivity extends SideBar {

    private final String DEBUG_TAG = getClass().getSimpleName();
    EditText editText; // the input textbox
    String[] searchResults = new String[5]; // Options to be shown in list view
    JSONArray resultsArr; // the results of the query
    String userId; // retrieves user's unique Id from previous intent for use
    MovieBackend movieBackend = new MovieBackend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        super.onCreateDrawer();
        editText = (EditText) findViewById(R.id.editText);
        userId = getIntent().getStringExtra("user_id");
    }

    // Get JSON Object
    public void search(String query) {
        String url = Config.getSearchUrl(getBaseContext(), query);
        resultsArr = movieBackend.getThemoviedbResults(url, DEBUG_TAG, getBaseContext());
    }

    // This is where we display the results in the listView
    public void displayResults() {
        int max = Math.min(resultsArr.length(), 5);
        int offSet = 0;
        try {
            // Display the max of 5 or the number of search results
            for (int i = 0; i < max; i++) {
                // Pass the IMBD movie id to the details page
                String movieId = resultsArr.getJSONObject(i).get("id").toString();
                String[] queryArr = new String[1];
                queryArr[0] = Config.getMovieInfoUrl(getBaseContext(), movieId);
                AsyncTask search = new MovieBackend().execute(queryArr);
                JSONObject json = null;
                json = (JSONObject) search.get();
                String imbdId = json.get("imdb_id").toString();
                String title = resultsArr.getJSONObject(i).get("title").toString();
                String date = resultsArr.getJSONObject(i).get("release_date").toString();
                if (date.length() > 4) {
                    date = date.substring(0, 4);
                }
                else {
                    date = "N/A";
                }
                searchResults[i] = title + "  (" + date + ")";
            }
            // Fill the rest of the spaces if the number of results < 5
            for (int i = max; i < 5; i++) {
                // If there are no results, say so
                if (i == 0) {
                    searchResults[i] = Messages.getMessage(getBaseContext(), "movie.noMovie");
                    continue;
                }
                searchResults[i] = "";
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "movie.dbException"), e);
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.movie_list, searchResults);
        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
        // Make it clickable
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Do nothing if there is no result
                if (searchResults[position].equals("") || searchResults[position].equals(Messages.getMessage(getBaseContext(), "movie.noMovie"))) {
                    return;
                }
                // Pass the data of the clicked movie to the movieDetails class
                Intent i = new Intent(MovieActivity.this,  MovieDetailsActivity.class);
                i.putExtra("user_id", userId);
                try {
                    // Pass the IMBD movie id to the details page
                    String movieId = resultsArr.getJSONObject(position).get("id").toString();
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
                        Toast.makeText(MovieActivity.this, Messages.getMessage(getBaseContext(), "follow.detailsNotFound"),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (imbdId.charAt(imbdId.length() - 1) == '/') {
                        imbdId = imbdId.substring(0, imbdId.length() - 1);
                    }
                    i.putExtra("JSON_Data", imbdId);
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "movie.dbException"), e);
                }
                startActivity(i);
            }
        });
    }


    // Search button - performs search
    protected void onButtonPressed(View view) {
        String searchText = editText.getText().toString();
        if (searchText.equals("")) {
            return;
        }
        searchText = searchText.replace(" ", "%20");
        search(searchText);
        displayResults();
    }
}


