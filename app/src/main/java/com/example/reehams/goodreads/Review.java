package com.example.reehams.goodreads;

/**
 * Created by Ameya on 3/28/17.
 */

public class Review {

    String id;
    String name;
    String personId;
    String personName;
    String reviewText;
    String rating;


    public Review(String id, String name, String personId, String personName, String rating, String reviewText) {
        this.id = id;
        this.name = name;
        this.personId = personId;
        this.personName = personName;
        this.reviewText = reviewText;
        this.rating = rating;
    }

}
