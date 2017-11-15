package edu.upenn.goodwatch.LayoutClasses;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import edu.upenn.goodwatch.DownloadImageTask;
import edu.upenn.goodwatch.FileAccess.Messages;
import edu.upenn.goodwatch.R;
import edu.upenn.goodwatch.Review;
import edu.upenn.goodwatch.User;

/**
 * Created by raph on 08/11/17.
 */

public class ReviewListAdapter extends ArrayAdapter<Review> {

    private final Context context;
    private final ArrayList<Review> reviewsList;

    public ReviewListAdapter(Context context, ArrayList<Review> reviewsList) {

        super(context, R.layout.review_row, reviewsList);

        this.context = context;
        this.reviewsList = reviewsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.review_row, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.movieTitle);
        TextView ratingView = (TextView) rowView.findViewById(R.id.rating);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        ImageView profilePic = (ImageView) rowView.findViewById(R.id.profilePic);

        Review rev = reviewsList.get(position);
        titleView.setText(rev.getMovieTitle() + "\n" + rev.getStars());
        ratingView.setText("\"" + rev.getReviewText() + "\"");
        User user = rev.getUser();
        if (user != null) {
            nameView.setText(user.getName());
            new DownloadImageTask(profilePic).execute(user.getPhotoUrl(context));
        }


        return rowView;
    }
}