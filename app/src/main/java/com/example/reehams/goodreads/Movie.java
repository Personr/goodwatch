package com.example.reehams.goodreads;

/**
 * Created by reehams on 3/20/17.
 */

public class Movie {
    public String id;
    public String name;

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String returnID() {
        return this.id;
    }

}
