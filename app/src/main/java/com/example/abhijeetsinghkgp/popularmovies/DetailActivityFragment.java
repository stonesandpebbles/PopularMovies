package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String SCHEME = "http";
    private static final String IMAGE_BASE_URL = "image.tmdb.org";
    private static final String IMAGE_SIZE = "w185";
    private static final String MOVIE_DATA = "MovieData";

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent movieDetail = getActivity().getIntent();
        MovieData movieData = movieDetail.getParcelableExtra(MOVIE_DATA);
        TextView movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
        movieTitle.setText(movieData.getTitle());
        ImageView movieIcon = (ImageView)rootView.findViewById(R.id.movie_icon);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(IMAGE_BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath(IMAGE_SIZE)
                .appendEncodedPath(movieData.getPosterImageUrl());

        Picasso.with(getActivity()).load(builder.build().toString()).into(movieIcon);
        //TextView movieTime = (TextView)rootView.findViewById(R.id.movie_time);
        //movieTime.setText(movieData.get);

        TextView movieReleaseYear = (TextView)rootView.findViewById(R.id.movie_release_year);
        DateFormat releaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = null;
        try {
            releaseDate = releaseDateFormat.parse(movieData.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar date = new GregorianCalendar();
        date.setTime(releaseDate);
        Integer year = date.get(Calendar.YEAR);
        movieReleaseYear.setText(year.toString());
        TextView movieRating = (TextView)rootView.findViewById(R.id.movie_rating);
        movieRating.setText(movieData.getRating() + "/10");
        TextView moviePlot = (TextView)rootView.findViewById(R.id.movie_plot);
        moviePlot.setText(movieData.getPlot());

        return rootView;
    }
}
