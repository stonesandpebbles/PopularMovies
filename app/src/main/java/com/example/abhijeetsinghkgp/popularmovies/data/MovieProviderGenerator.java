package com.example.abhijeetsinghkgp.popularmovies.data;

import android.net.Uri;

import com.example.abhijeetsinghkgp.popularmovies.FetchMoviesTask;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Abhijeet on 23-04-2016.
 */
@ContentProvider(authority = MovieProviderGenerator.AUTHORITY, database = MovieDatabaseGenerator.class, packageName = "com.example.abhijeetsinghkgp.popularmovies.data", name = "MovieProvider")
public final class MovieProviderGenerator {

    public static final String AUTHORITY =
            "com.example.abhijeetsinghkgp.popularmovies.data.MovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    interface Path{

        String MOVIE_TRAILERS = "movie_trailers";

        String MOVIES = "movies";

        String MOVIE_REVIEWS = "movie_reviews";

        String MOVIE_PATH_TOP_RATED = "top_rated_sw";

        String MOVIE_PATH_POPULAR = "popular_sw";

        String MOVIE_PATH_BOOKMARKED = "bookmarked_sw";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabaseGenerator.MOVIES) public static class Movies{
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/list",
                defaultSort = MovieColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = MovieColumns._ID,
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/list",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)
        public static Uri withId(String id){
            return buildUri(Path.MOVIES, id);
        }

        @InexactContentUri(
                name = MovieColumns.TOP_RATED_SW,
                path = Path.MOVIES + "/" + Path.MOVIE_PATH_TOP_RATED + "/" + FetchMoviesTask.YES,
                type = "vnd.android.cursor.dir/list",
                whereColumn = MovieColumns.TOP_RATED_SW,
                pathSegment = 2)
        public static Uri withTopRated(String sw){
            return buildUri(Path.MOVIES, Path.MOVIE_PATH_TOP_RATED, sw);
        }

        @InexactContentUri(
                name = MovieColumns.POPULAR_SW,
                path = Path.MOVIES + "/" + Path.MOVIE_PATH_POPULAR+ "/" + FetchMoviesTask.YES,
                type = "vnd.android.cursor.dir/list",
                whereColumn = MovieColumns.POPULAR_SW,
                pathSegment = 2)
        public static Uri withPopular(String sw){
            return buildUri(Path.MOVIES, Path.MOVIE_PATH_POPULAR, sw);
        }

        @InexactContentUri(
                name = MovieColumns.BOOKMARKED_SW,
                path = Path.MOVIES + "/" + Path.MOVIE_PATH_BOOKMARKED + "/" + FetchMoviesTask.YES,
                type = "vnd.android.cursor.dir/list",
                whereColumn = MovieColumns.BOOKMARKED_SW,
                pathSegment = 2)
        public static Uri withBookMarked(String sw){
            return buildUri(Path.MOVIES, Path.MOVIE_PATH_BOOKMARKED, sw);
        }
    }




    @TableEndpoint(table = MovieDatabaseGenerator.MOVIE_TRAILERS) public static class MovieTrailers{
        @ContentUri(
                path = Path.MOVIE_TRAILERS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = MovieTrailerColumns.TRAILER_NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_TRAILERS);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIE_TRAILERS + "/#",
                type = "vnd.android.cursor.dir/list",
                whereColumn = MovieTrailerColumns.MOVIE_ID,
                join = MovieDatabaseGenerator.MOVIE_REVIEWS,
                pathSegment = 1)
        public static Uri withId(String id){
            return buildUri(Path.MOVIE_TRAILERS, id);
        }
    }

    @TableEndpoint(table = MovieDatabaseGenerator.MOVIE_REVIEWS) public static class MovieReviews{
        @ContentUri(
                path = Path.MOVIE_REVIEWS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = MovieReviewColumns.AUTHOR + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_REVIEWS);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIE_REVIEWS + "/#",
                type = "vnd.android.cursor.dir/list",
                whereColumn = MovieReviewColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(String id){
            return buildUri(Path.MOVIE_REVIEWS, id);
        }
    }
}
