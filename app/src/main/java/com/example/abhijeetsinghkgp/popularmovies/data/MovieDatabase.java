package com.example.abhijeetsinghkgp.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Abhijeet on 23-04-2016.
 */

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {

    private MovieDatabase() {
    }

    public static final int VERSION = 1;

    @Table(MovieColumns.class) public static final String MOVIES = "movies";

    @Table(MovieTrailerColumns.class) public static final String MOVIE_TRAILERS =
            "movie_trailers";


    @Table(MovieReviewColumns.class) public static final String MOVIE_REVIEWS =
            "movie_reviews";
}

