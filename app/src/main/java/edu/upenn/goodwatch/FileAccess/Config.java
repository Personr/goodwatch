package edu.upenn.goodwatch.FileAccess;

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

    public static String getBrowseUrl(Context context) {
        return getProperty(context, "browsingURL", fileName);
    }

    public static String getActorIdGetterUrl(Context context) {
        return getProperty(context, "actorIdUrl", fileName);
    }

    public static String getActorSummaryGetterUrlStart(Context context) {
        return getProperty(context, "actorSummaryUrlStart", fileName);
    }

    public static String getActorSummaryGetterUrlEnd(Context context) {
        return getProperty(context, "actorSummaryUrlEnd", fileName);
    }

    public static String getGenreIdList(Context context) {
        return getProperty(context, "GenreIdListGetter", fileName);
    }

    public static String getMovieFromGenreId(Context context) {
        return getProperty(context, "GenreMovieGetter", fileName);
    }

    public static String keywordMovieGetter(Context context) {
        return getProperty(context, "KeywordMovieGetter", fileName);
    }

    public static String getDefaultProfilePicUrl(Context context) {
        return getProperty(context, "defaultProfilePic", fileName);
    }
}
