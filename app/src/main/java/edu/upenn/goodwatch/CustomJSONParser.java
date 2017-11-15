package edu.upenn.goodwatch;


import android.util.Log;

import java.util.ArrayList;

//Custom parser utility class
public class CustomJSONParser {


    public static String getYear(String input) {
        String regex = "\"year\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static String getName(String input) {
        String regex = "\"name\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static String getPosterURL(String input) {
        String regex = "\"poster_url\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex).replace("\\/", "/");
    }

    public static String getPlot(String input) {
        String regex = "\"plot\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static String getDirector(String input) {
        String regex = "\"director\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static String getRating(String input) {
        String regex = "\"rating\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static String getActors(String input) {
        String regex = "\"stars\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static String getGenre(String input) {
        String regex = "\"genre\":\"";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return temp.substring(0, endIndex);
    }

    public static int getID(String input) {
        String regex = "\"id\":";
        int index = input.indexOf(regex);
        String temp = input.substring(index + regex.length(), input.length());
        int endIndex = temp.indexOf('"');
        return Integer.parseInt(temp.substring(0, endIndex - 1));
    }

    public static ArrayList<String> getMovieList(String input) {
        ArrayList<String> movieList = new ArrayList<String>();
        while (true) {
            String regex = "\"original_title\":\"";
            int index = input.indexOf(regex);
            if (index < 0) break;
            input = input.substring(index + regex.length(), input.length());
            int endIndex = input.indexOf('"');
            String movie = input.substring(0, endIndex);
            movieList.add(movie);
            input = input.substring(endIndex, input.length());
            if (movieList.size() >= 10) break;
        }
        return movieList;
    }

    public static ArrayList<String> getMovieListKeyword(String input) {
        ArrayList<String> movieList = new ArrayList<String>();
        while (true) {
            String regex = "\"title\":\"";
            int index = input.indexOf(regex);
            if (index < 0) break;
            input = input.substring(index + regex.length(), input.length());
            int endIndex = input.indexOf('"');
            String movie = input.substring(0, endIndex);
            movieList.add(movie);
            input = input.substring(endIndex, input.length());
            if (movieList.size() >= 10) break;
        }
        return movieList;
    }

    public static int findGenreFromId(String input, String targetGenre) {
        //Parse the string till found
        int targetId = -1;

        while (true) {
            String regex = "\"id\":";
            int index = input.indexOf(regex);
            if (index < 0) break;
            input = input.substring(index + regex.length(), input.length());
            int endIndex = input.indexOf(',');
            int ID = Integer.parseInt(input.substring(0, endIndex));
            Log.v(null, "Herez");
            Log.v(null, ID + "");

            input = input.substring(endIndex, input.length());

            //Now Get the genre to see if this is the target
            regex = "\"name\":\"";
            index = input.indexOf(regex);
            if (index < 0) break;
            input = input.substring(index + regex.length(), input.length());
            endIndex = input.indexOf('"');
            String name = input.substring(0, endIndex);

            if (name.equals(targetGenre)) {
                targetId = ID;
                break;
            }
        }
        return targetId;
    }


}
