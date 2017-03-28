package com.example.reehams.goodreads;

/**
 * Created by reehams on 3/20/17.
 */

public class Movie {
    public String id;
    public String name;
    public String personName;
    String personId;

    public Movie(String id, String name, String personId, String personName) {
        this.id = id;
        this.name = name;
        this.personId = personId;
        this.personName = personName;
    }

    public String returnID() {
        return this.id;
    }

}
