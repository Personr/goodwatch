package com.example.reehams.goodreads;

import java.util.*;

/**
 * Created by reehams on 3/15/17.
 */

public class User {
    public String email;
    public String name;
    public String id;
    public List<String> watchlist;
    public List<String> followingIds;
    public List<String> followerIds;
    public List<Review> reviews;





    public User(String name, String email,  String id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public User(String name, String email,  String id, List l) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.reviews = l;
        this.watchlist = l;
        this.followingIds = l;
        this.followerIds = l;
    }


    public User(String name, String email,  String id, List<String> watchlist, List<Review> reviews,
                List<String> followingIds , List<String> followerIds) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.reviews = reviews;
        this.watchlist = watchlist;
        this.followingIds = followingIds;
        this.followerIds = followerIds;
    }


    public User(String name, String email,  String id, String x, String y) {
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public String toString() {
        return this.name;
    }
}
