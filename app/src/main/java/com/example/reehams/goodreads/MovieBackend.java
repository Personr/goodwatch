package com.example.reehams.goodreads;

/**
 * Created by rahulkooverjee on 2/20/17.
 */

import android.os.AsyncTask;

import org.json.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Exchanger;

public class MovieBackend extends AsyncTask<String, Void, JSONObject> {

    // Returns the JSON Object as a result of search query
    @Override
    protected JSONObject doInBackground(String... strings) {
        try {
            String query = strings[0];
            URL url = new URL(query);
            Scanner scanner = new Scanner(url.openStream());
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
}
