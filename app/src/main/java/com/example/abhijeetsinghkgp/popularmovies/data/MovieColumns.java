package com.example.abhijeetsinghkgp.popularmovies.data;

import com.example.abhijeetsinghkgp.popularmovies.FetchMoviesTask;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Abhijeet on 23-04-2016.
 */
public interface MovieColumns {
    @DataType(DataType.Type.TEXT)@PrimaryKey
    public static final String _ID = "_id";


    @DataType(DataType.Type.TEXT)@NotNull
    public static final String MOVIE_TITLE = "movie_title";
    @DataType(DataType.Type.TEXT)@NotNull
    public static final String MOVIE_PLOT = "movie_plot";


    @DataType(DataType.Type.TEXT)@NotNull
    public static final String MOVIE_RATING = "movie_rating";

    @DataType(DataType.Type.TEXT)@NotNull
    public static final String MOVIE_RELEASE = "movie_release_date";

    @DataType(DataType.Type.TEXT)@NotNull public static final String POSTER_IMG_URL = "poster_url";
    @DataType(DataType.Type.TEXT)@NotNull public static final String BACKDROP_IMG_URL = "backdrop_url";

    @DataType(DataType.Type.TEXT)@NotNull @DefaultValue(FetchMoviesTask.NO)
    public static final String TOP_RATED_SW = "top_rated_sw";

    @DataType(DataType.Type.TEXT)@NotNull @DefaultValue(FetchMoviesTask.NO)
    public static final String POPULAR_SW = "popular_sw";

    @DataType(DataType.Type.TEXT)@NotNull @DefaultValue(FetchMoviesTask.NO)
    public static final String BOOKMARKED_SW = "bookmarked_sw";
}
