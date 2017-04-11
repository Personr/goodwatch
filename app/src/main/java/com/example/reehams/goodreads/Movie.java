package com.example.reehams.goodreads;

import java.util.*;
/**
 * Created by reehams on 3/20/17.
 */

public class Movie {
    public String id;
    public String name;
    List<Review> reviews = new ArrayList<Review>();

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
        reviews.add(new Review("null"));
    }
}
