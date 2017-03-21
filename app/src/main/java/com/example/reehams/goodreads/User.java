package com.example.reehams.goodreads;

/**
 * Created by reehams on 3/15/17.
 */

public class User {
    public String email;
    public String name;
    public String userId;

    public User(String name, String email, String userId) {
        this.email = email;
        this.name = name;
        this.userId = userId;
    }
}
