package com.example.reehams.goodreads.FileAccess;

/**
 * Created by raph on 31/10/17.
 */

import android.content.Context;

public class Config extends FileAccesser {

    private static final String fileName = "config.properties";

    public static String getSearchUrl(Context context, String query) {
        String url = getProperty(context, "searchAddress", fileName);
        url = url.replace("%API_KEY%", getProperty(context, "themoviedbApiKey", fileName));
        url = url.replace("%QUERY%", query);
        return url;
    }

    public static String getMovieInfoUrl(Context context, String movieId) {
        String url = getProperty(context, "movieInfoAddress", fileName);
        url = url.replace("%API_KEY%", getProperty(context, "themoviedbApiKey", fileName));
        url = url.replace("%MOVIE_ID%", movieId);
        return url;
    }

    public static String getApacheUrl(Context context, String movieId) {
        String url = getProperty(context, "theapache64Address", fileName);
        url = url.replace("%MOVIE_ID%", movieId);
        return url;
    }

    public static String getDefaultPoster(Context context) {
        return getProperty(context, "defaultPosterUrl", fileName);
    }
}
