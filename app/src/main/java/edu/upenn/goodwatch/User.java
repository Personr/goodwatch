package edu.upenn.goodwatch;

import android.content.Context;

import java.util.*;

import edu.upenn.goodwatch.FileAccess.Config;

/**
 * Created by reehams on 3/15/17.
 */

public class User {
    public String email;
    public String name;
    public String id;
    public String photoUrl;
    public List<String> watchlist;
    public List<String> followingIds;
    public List<String> followerIds;
    public List<Review> reviews;


    public User(String name, String email,  String id, String photoUrl) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.photoUrl = photoUrl;
    }

    public User(String name, String email,  String id, String photoUrl, List<String> watchlist, List<Review> reviews,
                List<String> followingIds , List<String> followerIds) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.photoUrl = photoUrl;
        this.reviews = reviews;
        this.watchlist = watchlist;
        this.followingIds = followingIds;
        this.followerIds = followerIds;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl(Context context) {
        if (photoUrl == null) {
            photoUrl = Config.getDefaultProfilePicUrl(context);
        }
        return photoUrl;
    }

    public  String getId() {
        return id;
    }

    public String toString() {
        return this.name;
    }
}
