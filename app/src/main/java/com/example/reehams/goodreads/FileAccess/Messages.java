package com.example.reehams.goodreads.FileAccess;

import android.content.Context;

/**
 * Created by raph on 31/10/17.
 */

public class Messages extends FileAccesser {

    private static final String fileName = "messages.properties";

    public static String getMessage(Context context, String msgName) {
        return getProperty(context, msgName, fileName);
    }

    public static String noReview(Context context, String name) {
        String message = getProperty(context, "follow.noReview", fileName);
        return message.replace("%NAME%", name);
    }

    public static String reviewBelow(Context context, String name) {
        String message = getProperty(context, "review.reviewBelow", fileName);
        return message.replace("%NAME%", (name != null ? name : "null"));
    }

    public static String averageRating(Context context, String name, String avgRating) {
        String message = getProperty(context, "topcharts.average", fileName);
        message = message.replace("%NAME%", (name != null ? name : "null"));
        return message.replace("%AVERAGE%", avgRating);
    }

}
