package com.example.abhijeetsinghkgp.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieColumns;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;

public class DetailActivity extends AppCompatActivity {
    private static final String MOVIE_DATA = "MovieData";
    private static final String MOVIE_DATA_BOOKMARKED = "MovieDataBookMarked";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void handleChange(View view){
        Intent movieDetail = getIntent();
        MovieData movieData = movieDetail.getParcelableExtra(MOVIE_DATA);
        int starBackgroundColor = R.color.yellow;
        ImageButton starIcon = (ImageButton)view.findViewById(R.id.star_icon);
        if(movieData.getBookMarkedSw().equalsIgnoreCase(FetchMoviesTask.YES)) {
            movieData.setBookMarkedSw(FetchMoviesTask.NO);
            starBackgroundColor = R.color.grey;
        }
        else {
            movieData.setBookMarkedSw(FetchMoviesTask.YES);
            starBackgroundColor = R.color.yellow;
        }
        starIcon.setBackgroundColor(getResources().getColor(starBackgroundColor));
        ContentValues movieValues = new ContentValues();
        movieDetail.putExtra(MOVIE_DATA, movieData);
        movieValues.put(MovieColumns.POPULAR_SW, movieData.getPopularSw());
        movieValues.put(MovieColumns.BOOKMARKED_SW, movieData.getBookMarkedSw());
        movieValues.put(MovieColumns.TOP_RATED_SW, movieData.getTopRatedSw());
        movieValues.put(MovieColumns._ID, movieData.getId());
        movieValues.put(MovieColumns.BACKDROP_IMG_URL, movieData.getBackdropImageUrl());
        movieValues.put(MovieColumns.MOVIE_PLOT, movieData.getPlot());
        movieValues.put(MovieColumns.MOVIE_RATING, movieData.getRating());
        movieValues.put(MovieColumns.MOVIE_RELEASE, movieData.getReleaseDate());
        movieValues.put(MovieColumns.MOVIE_TITLE, movieData.getTitle());
        movieValues.put(MovieColumns.POSTER_IMG_URL, movieData.getPosterImageUrl());
        getContentResolver().update(MovieProviderGenerator.Movies.withId(movieData.getId()), movieValues, null , null);
        getSupportParentActivityIntent().putExtra(MOVIE_DATA_BOOKMARKED, movieData);
    }
}
