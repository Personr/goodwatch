package com.example.reehams.goodreads;

/**
 * Created by reehams on 3/15/17.
 */

public class User {
    public String email;
    public String name;
    public String id;
    public String profilePicId;

    public User(String name, String email, String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public String toString() {
        return this.name;
    }
}
