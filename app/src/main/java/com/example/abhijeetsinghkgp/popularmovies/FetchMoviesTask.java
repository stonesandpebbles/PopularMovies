package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieProvider;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhijeet on 12-03-2016.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<MovieData> >{
    public static final String YES = "Y";
    public static final String NO = "N";
    private static final String POPULAR = "popular?";
    private static final String HIGHEST_RATED = "top_rated?";
    public static final String MOVIE_TYPE_POPULAR = "POPULAR";
    public static final String MOVIE_TYPE_TOP_RATED = "TOP_RATED";
    private static final String REVIEWS = "reviews";
    private static final String TRAILERS = "videos";
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "api_key";
    private static final String APPEND_TO_RESPONSE = "append_to_response";
    private static final Uri popularBuiltUri = Uri.parse(MOVIE_BASE_URL + POPULAR).buildUpon()
            .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
            .build();
    private static final Uri topRatedBuiltUri = Uri.parse(MOVIE_BASE_URL + HIGHEST_RATED).buildUpon()
            .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
            .build();
    private static final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private MovieAdapter moviesAdapter;
    private Context mContext;
    private View movieView;
    public FetchMoviesTask(View view, Context context){
        mContext = context;
        movieView = view;
    }
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<MovieData> doInBackground(String... params) {
        //android.os.Debug.waitForDebugger();
        List<MovieData> movieDataList = new ArrayList<>();


        //Log.v("TMDB API REQ", builtUri.toString());
        URL url = null;
        MovieDataParser movieDataParser = new MovieDataParser();
        try {
            url = new URL(topRatedBuiltUri.toString());
            String movieTopJsonStr = getMovieDetailsFromAPI(url);
            movieDataParser.getMovieDataFromJson(movieDataList, movieTopJsonStr, MOVIE_TYPE_TOP_RATED);

            url = new URL(popularBuiltUri.toString());
            String moviePopJsonStr = getMovieDetailsFromAPI(url);
            movieDataParser.getMovieDataFromJson(movieDataList, moviePopJsonStr, MOVIE_TYPE_POPULAR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        List<String> movieDetailJsonStrList = new ArrayList<>();
        URL movieDetailUrl = null;

        for(int i = 0; i < movieDataList.size(); i++){
            try {
                movieDetailUrl = new URL(Uri.parse(MOVIE_BASE_URL + movieDataList.get(i).getId()).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .appendQueryParameter(APPEND_TO_RESPONSE, REVIEWS + "," + TRAILERS)
                        .build().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            movieDetailJsonStrList.add(getMovieDetailsFromAPI(movieDetailUrl));
        }
        try {
            movieDataParser.getMovieDetailData(movieDataList, movieDetailJsonStrList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        movieDataParser.insertIntoDB(movieDataList, mContext);
        return movieDataList;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param movieDataList The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<MovieData> movieDataList) {
        super.onPostExecute(movieDataList);

//        moviesAdapter = new MovieAdapter(mContext, movieDataList);
//        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) movieView;
        mContext.getContentResolver().notifyChange(MovieProvider.Movies.CONTENT_URI, null);
    }

    private String getMovieDetailsFromAPI(URL url){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            // Construct the URL for the themoviedb api query


            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return movieJsonStr;
        }
    }
}