package com.example.reehams.goodreads;

/**
 * Created by reehams on 3/27/17.
 */

public class Following {
    String id;
    String personId;
    String personName;
    String name;

    public Following(String id, String personId, String name,String personName) {
        this.id = id;
        this.name = name;
        this.personName = personName;
        this.personId = personId;
    }
}
