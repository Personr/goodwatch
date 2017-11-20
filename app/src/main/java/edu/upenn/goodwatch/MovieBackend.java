package edu.upenn.goodwatch;

/**
 * Created by rahulkooverjee on 2/20/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;

public class MovieBackend extends AsyncTask<String, Void, JSONObject> {

    // Returns the JSON Object as a result of search query
    @Override
    protected JSONObject doInBackground(String... strings) {
        try {
            String query = strings[0];
            URL url = new URL(query);
            Scanner scanner = new Scanner(url.openConnection().getInputStream());
            StringBuilder responseBuilder = new StringBuilder();
            while(scanner.hasNextLine()) {
                responseBuilder.append(scanner.nextLine());
            }
            String response = responseBuilder.toString();
            JSONTokener tokener = new JSONTokener(response);
            JSONObject json = new JSONObject(tokener);
            return json;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getPoster(String URL) {
        String[] queryArr = new String[1];
        queryArr[0] = URL;
        AsyncTask search = new URLThread().execute(queryArr);
        try {
            return (Bitmap) search.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getMovieDetails(String movieId) {
        String[] queryArr = new String[1];
        queryArr[0] = movieId;
        AsyncTask search = new URLThread().execute(queryArr);
        try {
            return (JSONObject) search.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class URLThread extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                String query = strings[0];
                if (query.equals("n/a")) {
                    return null;
                }
                URL url = new URL(query);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public JSONObject queryThemoviedb(String url, String DEBUG_TAG, Context context) {
        // Set up query
        String[] queryArr = new String[1];
        queryArr[0] = url;
        AsyncTask search = execute(queryArr);
        JSONObject json = null;
        // Search and add
        try {
            // Get search results as a JSONArray
            json = (JSONObject) search.get();
        } catch (Exception e) {
            Log.e(DEBUG_TAG, Messages.getMessage(context, "movie.dbException"), e);
        }

        return json;
    }

    public JSONArray getThemoviedbResults(String url, String DEBUG_TAG, Context context) {
        JSONArray resultsArr = null;
        try {
            resultsArr = queryThemoviedb(url, DEBUG_TAG, context).getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultsArr;
    }

    public ArrayList<Movie> getMovieArray(String url, String DEBUG_TAG, Context context) {
        JSONArray resultsArr = getThemoviedbResults(url, DEBUG_TAG, context);

        ArrayList<Movie> movies = new ArrayList<>();
        for (int i = 0; i < resultsArr.length(); i++) {
            // Pass the IMBD movie id to the details page
            try {
                String movieId = resultsArr.getJSONObject(i).get("id").toString();
                movies.add(new Movie(movieId, DEBUG_TAG, context));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return movies;
    }
}
