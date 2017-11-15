package edu.upenn.goodwatch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONObject;
import android.widget.ArrayAdapter;
import java.net.*;
import java.io.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;


public class BrowseActivity extends AppCompatActivity {
    private boolean isActorsChecked = false;
    private boolean isGenreChecked = false;
    private boolean isKeywordChecked = false;
    private String searchQuery = "";
    private final String DEBUG_TAG = this.getClass().getName();
    private String apiString = "https://api.themoviedb.org/3/search/movie?api_key=e0f662e5cbaf753c72140fec0a8b1638&query=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse2);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Get Checkboxes
        CheckBox actorCheckbox = (CheckBox) findViewById(R.id.ActorsCheckBox);
        CheckBox genreCheckbox = (CheckBox) findViewById(R.id.GenreCheckbox);
        CheckBox keywordCheckbox = (CheckBox) findViewById(R.id.KeywordCheckbox);
        final EditText searchInput = (EditText)  findViewById(R.id.SearchCriteriaText);
        Button submitButton = (Button) findViewById(R.id.movieSearchButton);

        final ListView results = (ListView) findViewById(R.id.searchresults);


        //Activate Event Listeners
        actorCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox) view;
                if (c.isChecked()) isActorsChecked = true;
                else isActorsChecked = false;
            }
        });

        genreCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox) view;
                if (c.isChecked()) isGenreChecked = true;
                else isGenreChecked = false;
            }
        });

        keywordCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox) view;
                if (c.isChecked()) isKeywordChecked = true;
                else isKeywordChecked = false;
            }
        });

        //Event Listener for the text view
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            //@Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = searchInput.getText().toString();

            }
        });

        //Submit Listener for the search button
        final Context context = this;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Format the string for querying
                searchQuery = searchQuery.trim();
                searchQuery = searchQuery.replace(' ', '+');
                searchQuery = searchQuery.toLowerCase();

                //Check if only param is on, currently only one search is allowed
                if ((isActorsChecked && isGenreChecked) || (isKeywordChecked && isActorsChecked)
                        || (isGenreChecked && isKeywordChecked)) {
                    String message = "Please only check one box!";
                    displayToast(message);
                }

                //Error handling when no criteria is specified
                else if(!(isGenreChecked || isKeywordChecked || isActorsChecked)) {
                    String message = "Please check a box!";
                    displayToast(message);
                }

                //Implement Logic if a search by actor is done
                else if (isActorsChecked) {
                    //Get actor id
                    String newQuery = Config.getActorIdGetterUrl(getBaseContext()) + searchQuery;
                    String idContents = ExecuteQuery(newQuery);
                    int id = CustomJSONParser.getID(idContents);

                    //Now get all movies
                    String actorSummaryQuery = Config.getActorSummaryGetterUrlStart(getBaseContext())
                            + id + Config.getActorSummaryGetterUrlEnd(getBaseContext());
                    String actorSummary = ExecuteQuery(actorSummaryQuery);
                    ArrayList<String> movieList = CustomJSONParser.getMovieList(actorSummary);

                    if (movieList.size() == 0) getToastMessageFromMovieList(movieList);

                    else {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, movieList);
                        results.setAdapter(arrayAdapter);
                    }

                }
                else if (isGenreChecked) {
                    String newQuery = Config.getGenreIdList(getBaseContext());
                    String idContents = ExecuteQuery(newQuery);
                    idContents = idContents.toLowerCase();
                    
                    //Parse the response to find Id of the movie
                    int genreID = CustomJSONParser.findGenreFromId(idContents, searchQuery);

                    String genreQuery = Config.getMovieFromGenreId(getBaseContext()) + genreID;
                    String response = ExecuteQuery(genreQuery);

                    ArrayList<String> movieList = CustomJSONParser.getMovieList(response);

                    if (movieList.size() == 0) getToastMessageFromMovieList(movieList);

                    else {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, movieList);
                        results.setAdapter(arrayAdapter);
                    }
                }
                else if (isKeywordChecked) {
                    String query = Config.keywordMovieGetter(getBaseContext()) + searchQuery;
                    String response = ExecuteQuery(query);
                    ArrayList<String> movieList = CustomJSONParser.getMovieListKeyword(response);

                    if (movieList.size() == 0) getToastMessageFromMovieList(movieList);

                    else {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, movieList);
                        results.setAdapter(arrayAdapter);
                    }
                }
            }
        });

    }

    public String ExecuteQuery(String query) {
        String[] queryArr = new String[1];
        queryArr[0] = query;
        String contents = "";
        AsyncTask search = new MovieBackend().execute(queryArr);
        JSONObject json = null;
        try {
            json = (JSONObject) search.get();
            contents = json.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return  contents;
    }

    public void displayToast(String toToast) {
        Toast.makeText(BrowseActivity.this, toToast,
                Toast.LENGTH_LONG).show();
    }

    //Creates toast message
    public String getToastMessageFromMovieList(ArrayList<String> movieList) {
        if (movieList.size() == 0) return "Sorry no matches found!";
        String results = "You should checkout these movies! \n";
        for (int i = 0; i < movieList.size(); i++) {
            results = results + movieList.get(i) + "\n";
        }
        return  results;
    }


}
