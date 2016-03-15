package com.example.abhijeetsinghkgp.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Abhijeet on 12-03-2016.
 */
public class MovieDataParser {
    private static final String RESULT_LIST = "results";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_BACKDROP_PATH = "backdrop_path";
    private static final String MOVIE_PLOT = "overview";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_TITLE = "original_title";


    public List<MovieData> getMovieDataFromJson(String movieJsonStr) throws JSONException {
        //no need for arrayList here as order doesn't matter
        List<MovieData> movieDataList = new LinkedList<>();
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = movieJson.getJSONArray(RESULT_LIST);

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            MovieData movieData = new MovieData();
            movieData.setPosterImageUrl(movie.getString(MOVIE_POSTER_PATH));
            movieData.setBackdropImageUrl(movie.getString(MOVIE_BACKDROP_PATH));
            movieData.setPlot(movie.getString(MOVIE_PLOT));
            movieData.setReleaseDate(movie.getString(MOVIE_RELEASE_DATE));
            movieData.setRating(movie.getString(MOVIE_RATING));
            movieData.setTitle(movie.getString(MOVIE_TITLE));
            movieDataList.add(movieData);
        }
        return movieDataList;
    }
}
