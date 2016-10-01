package com.example.abhijeetsinghkgp.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.util.Log;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieColumns;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieReviewColumns;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieTrailerColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Abhijeet on 12-03-2016.
 */
public class MovieDataParser {
    private static final String RESULT_LIST = "results";
    private static final String TRAILER_LIST = "videos";
    private static final String REVIEW_LIST = "reviews";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_BACKDROP_PATH = "backdrop_path";
    private static final String MOVIE_PLOT = "overview";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "original_title";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";
    private static final String TRAILER_TYPE = "type";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";
    private static final String ID = "id";
    private static final String LOG_TAG = MovieDataParser.class.getSimpleName();


    public void getMovieDataFromJson(List<MovieData> movieDataList, String movieJsonStr, String movieType) throws JSONException {
        //no need for arrayList here as order doesn't matter
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = movieJson.getJSONArray(RESULT_LIST);
        Map<String, Integer> movieIdMap = new HashMap<>();
        for(int j = 0; j < movieDataList.size(); j++){
            movieIdMap.put(movieDataList.get(j).getId(), j);
        }

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            MovieData movieData = new MovieData();
            movieData.setPosterImageUrl(movie.getString(MOVIE_POSTER_PATH));
            movieData.setBackdropImageUrl(movie.getString(MOVIE_BACKDROP_PATH));
            movieData.setPlot(movie.getString(MOVIE_PLOT));
            movieData.setReleaseDate(movie.getString(MOVIE_RELEASE_DATE));
            movieData.setRating(movie.getString(MOVIE_RATING));
            movieData.setTitle(movie.getString(MOVIE_TITLE));
            movieData.setId(movie.getString(MOVIE_ID));
            String topRatedSw = movieType.equalsIgnoreCase(FetchMoviesTask.MOVIE_TYPE_TOP_RATED)? FetchMoviesTask.YES:FetchMoviesTask.NO;
            String popularSw = movieType.equalsIgnoreCase(FetchMoviesTask.MOVIE_TYPE_POPULAR)? FetchMoviesTask.YES:FetchMoviesTask.NO;
            movieData.setTopRatedSw(topRatedSw);
            movieData.setPopularSw(popularSw);
            if(movieIdMap.containsKey(movieData.getId())){
                MovieData movieDataDup = movieDataList.get(movieIdMap.get(movieData.getId()));
                movieDataDup.setTopRatedSw(FetchMoviesTask.YES);
                movieDataDup.setPopularSw(FetchMoviesTask.YES);
            }
            else{
                movieDataList.add(movieData);
            }

        }
    }


    public void getMovieDetailData(List<MovieData> movieDataList, List<String> movieDetailJsonStrList) throws JSONException {
        for(int i = 0; i < movieDetailJsonStrList.size(); i++){
            JSONObject movieDetailJson = new JSONObject(movieDetailJsonStrList.get(i));
            JSONObject trailerObject = movieDetailJson.getJSONObject(TRAILER_LIST);
            JSONArray trailerListArray = trailerObject.getJSONArray(RESULT_LIST);
            JSONObject reviewObject = movieDetailJson.getJSONObject(REVIEW_LIST);
            JSONArray reviewListArray = reviewObject.getJSONArray(RESULT_LIST);
            List<TrailerData> trailerDataList = new ArrayList<>();
            List<ReviewData> reviewDataList = new ArrayList<>();
            for(int k = 0; k < trailerListArray.length(); k++){
                JSONObject trailer = trailerListArray.getJSONObject(k);
                TrailerData trailerData = new TrailerData();
                trailerData.setKey(trailer.getString(TRAILER_KEY));
                trailerData.setName(trailer.getString(TRAILER_NAME));
                trailerData.setType(trailer.getString(TRAILER_TYPE));
                trailerData.setId(trailer.getString(ID));
                trailerDataList.add(trailerData);
            }

            for(int j = 0; j < reviewListArray.length(); j++){
                ReviewData reviewData = new ReviewData();
                JSONObject review = reviewListArray.getJSONObject(j);
                reviewData.setAuthor(review.getString(REVIEW_AUTHOR));
                reviewData.setContent(review.getString(REVIEW_CONTENT));
                reviewData.setUrl(review.getString(REVIEW_URL));
                reviewData.setId(review.getString(ID));
                reviewDataList.add(reviewData);
            }
            movieDataList.get(i).setTrailerDataList(trailerDataList);
            movieDataList.get(i).setReviewDataList(reviewDataList);
        }
    }


    public void insertIntoDB(List<MovieData> movieDataList, Context context) {
        Vector<ContentValues> movieCvVector = new Vector<ContentValues>(movieDataList.size());
        Vector<ContentValues> movieTrCvVector = new Vector<ContentValues>();
        Vector<ContentValues> movieRevCvVector = new Vector<ContentValues>();
        for(int i = 0; i < movieDataList.size(); i++) {
            MovieData movieData = movieDataList.get(i);
            ContentValues movieValues = new ContentValues();

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

            List<TrailerData> trailerDataList = movieData.getTrailerDataList();
            List<ReviewData> reviewDataList = movieData.getReviewDataList();
            for(int j = 0; j < trailerDataList.size(); j++){
                TrailerData trailerData = trailerDataList.get(j);
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieTrailerColumns._ID, trailerData.getId());
                trailerValues.put(MovieTrailerColumns.MOVIE_ID, movieData.getId());
                trailerValues.put(MovieTrailerColumns.TRAILER_NAME, trailerData.getName());
                trailerValues.put(MovieTrailerColumns.TRAILER_URL, trailerData.getKey());
                movieTrCvVector.add(trailerValues);
            }

            for(int j = 0; j < reviewDataList.size(); j++){
                ReviewData reviewData = reviewDataList.get(j);
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieReviewColumns._ID, reviewData.getId());
                reviewValues.put(MovieReviewColumns.AUTHOR, reviewData.getAuthor());
                reviewValues.put(MovieReviewColumns.CONTENT, reviewData.getContent());
                reviewValues.put(MovieReviewColumns.MOVIE_ID, movieData.getId());
                movieRevCvVector.add(reviewValues);
            }
            movieCvVector.add(movieValues);
        }
        insertData(MovieProviderGenerator.Movies.CONTENT_URI, movieCvVector, context);
       insertData(MovieProviderGenerator.MovieTrailers.CONTENT_URI, movieTrCvVector, context);
       insertData(MovieProviderGenerator.MovieReviews.CONTENT_URI, movieRevCvVector, context);


    }


    private void insertData(Uri uri, Vector<ContentValues> cVVector, Context context){
        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            try {
                // delete old data so we don't build up an endless history
                ContentResolver cr = context.getContentResolver();
                cr.getClass();
                context.getContentResolver().delete(uri, null, null);
                inserted = context.getContentResolver().bulkInsert(uri, cvArray);
            }
            catch (SQLiteConstraintException e){
                e.getLocalizedMessage();
            }



            //notifyWeather();
        }

        Log.d(LOG_TAG, "Insertion Complete. " +uri.toString() + inserted + " Inserted");
    }

}
