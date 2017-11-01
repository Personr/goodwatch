package com.example.reehams.goodreads;

/**
 * Created by raph on 31/10/17.
 */

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {

    private static Map<String, String> savedProp = new HashMap<>();

    private static String getProperty(Context context, String property) {
        String value = savedProp.get(property);
        if (value != null) {
            return value;
        }

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = context.getAssets().open("config.properties");
            prop.load(input);

            value = prop.getProperty(property);
            savedProp.put(property, value);
            return value;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getSearchUrl(Context context, String query) {
        String url = getProperty(context, "searchAddress");
        url = url.replace("%API_KEY%", getProperty(context, "themoviedbApiKey"));
        url = url.replace("%QUERY%", query);
        return url;
    }

    public static String getMovieInfoUrl(Context context, String movieId) {
        String url = getProperty(context, "movieInfoAddress");
        url = url.replace("%API_KEY%", getProperty(context, "themoviedbApiKey"));
        url = url.replace("%MOVIE_ID%", movieId);
        return url;
    }
}
