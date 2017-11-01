package edu.upenn.goodwatch;


//Parses badly formatted Json per request
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

}
