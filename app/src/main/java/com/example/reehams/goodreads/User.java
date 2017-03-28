package com.example.reehams.goodreads;

/**
 * Created by reehams on 3/15/17.
 */

public class User {
    public String email;
    public String name;
    public String id;
    public String personName;

    public User(String name, String email, String id, String personName) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.personName = personName;
    }

    public String toString() {
        return this.name;
    }
}
