package edu.upenn.goodwatch.LayoutClasses;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.upenn.goodwatch.CustomJSONParser;
import edu.upenn.goodwatch.DownloadImageTask;
import edu.upenn.goodwatch.FileAccess.Config;
import edu.upenn.goodwatch.FileAccess.Messages;
import edu.upenn.goodwatch.Movie;
import edu.upenn.goodwatch.MovieDetailsActivity;
import edu.upenn.goodwatch.R;

/**
 * Created by raph on 08/11/17.
 */

public class MovieGridAdapter extends ArrayAdapter<Movie> {

    private final Context context;
    private final ArrayList<Movie> movies;

    public MovieGridAdapter(Context context, ArrayList<Movie> movies) {

        super(context, R.layout.movie_preview, movies);

        this.context = context;
        this.movies = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View moviePreview = inflater.inflate(R.layout.movie_preview, parent, false);
        TextView movieName = (TextView) moviePreview.findViewById(R.id.movieName);
        ImageView moviePic = (ImageView) moviePreview.findViewById(R.id.moviePic);

        Movie movie = movies.get(position);
        new DownloadImageTask(moviePic).execute(movie.getPosterURL());
        movieName.setText(movie.getName());

        return moviePreview;
    }
}