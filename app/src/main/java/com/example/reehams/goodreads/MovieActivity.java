package com.example.reehams.goodreads;

import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.*;
import android.widget.AdapterView;
import org.json.*;

import java.io.InputStream;
import java.util.Scanner;


/**
 * Created by rahulkooverjee on 2/20/17.
 */

public class MovieActivity extends AppCompatActivity {

    EditText editText; // the input textbox
    String[] searchResults = new String[5]; // Options to be shown in list view
    JSONArray resultsArr; // the results of the query

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        editText = (EditText) findViewById(R.id.editText);
    }

    // Get JSON Object
    public void search(String query) {
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
            resultsArr = json.getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This is where we display the results in the listView
    public void displayResults() {
        int max = Math.min(resultsArr.length(), 5);
        try {
            // Display the max of 5 or the number of search results
            for (int i = 0; i < max; i++) {
                searchResults[i] = resultsArr.getJSONObject(i).get("title").toString() + "  (" +
                        resultsArr.getJSONObject(i).get("release_date").toString().substring(0, 4) + ")";
            }
            // Fill the rest of the spaces if the number of results < 5
            for (int i = max; i < 5; i++) {
                // If there are no results, say so
                if (i == 0) {
                    searchResults[i] = "No Results Found";
                    continue;
                }
                searchResults[i] = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                if (searchResults[position].equals("") || searchResults[position].equals("No Results Found")) {
                    return;
                }
                // Pass the data of the clicked movie to the movieDetails class
                Intent i = new Intent(MovieActivity.this,  MovieDetailsActivity.class);
                try {
                    // Pass the IMBD movie id to the details page
                    String movieId = resultsArr.getJSONObject(position).get("id").toString();
                    String[] queryArr = new String[1];
                    queryArr[0] = "https://api.themoviedb.org/3/movie/" + movieId +
                            "?api_key=9f4d052245dda68f14bcbd986787dc7b&language=en-US";
                    AsyncTask search = new MovieBackend().execute(queryArr);
                    JSONObject json = null;
                    json = (JSONObject) search.get();
                    i.putExtra("JSON_Data", json.get("imdb_id").toString());
                } catch (Exception e) {
                    e.printStackTrace();
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


