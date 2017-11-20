package edu.upenn.goodwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.goodwatch.CustomJSONParser;
import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.LayoutClasses.MovieGridAdapter;
import edu.upenn.goodwatch.R;
import edu.upenn.goodwatch.SideBar;

/**
 * Created by raph on 17/11/17.
 */

public class NewReleasesActivity extends SideBar {

    private final String DEBUG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_releases);
        super.onCreateDrawer();

        MovieBackend movieBackend = new MovieBackend();
        String url = Config.getNewReleasesUrl(getBaseContext());
        ArrayList<Movie> movies = movieBackend.getMovieArray(url, DEBUG_TAG, getBaseContext());

        GridView gridView = (GridView)findViewById(R.id.gridview);
        MovieGridAdapter movieAdapter = new MovieGridAdapter(getBaseContext(), movies);
        gridView.setAdapter(movieAdapter);
    }

}
