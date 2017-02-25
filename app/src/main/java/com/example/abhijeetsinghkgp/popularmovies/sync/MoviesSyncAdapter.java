package com.example.abhijeetsinghkgp.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.example.abhijeetsinghkgp.popularmovies.BuildConfig;
import com.example.abhijeetsinghkgp.popularmovies.FetchMoviesTask;
import com.example.abhijeetsinghkgp.popularmovies.MainActivity;
import com.example.abhijeetsinghkgp.popularmovies.MovieAdapter;
import com.example.abhijeetsinghkgp.popularmovies.MovieData;
import com.example.abhijeetsinghkgp.popularmovies.MovieDataParser;
import com.example.abhijeetsinghkgp.popularmovies.R;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;

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
 * Created by Abhijeet on 22-05-2016.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the movie list, in seconds.
    public static final int SYNC_INTERVAL_DAY = 60*60*24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL_DAY /3;

    private static final long DAY_IN_MILLIS = 1000 * 24;
    private static final int MOVIE_NOTIFICATION_ID = 3004;

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


    private void notifyMovies() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean displayNotifications = prefs.getBoolean("notifications_new_message",
                true);

        if (displayNotifications) {
            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the movies.

                int iconId = R.mipmap.movie_launcher;
                String title = context.getString(R.string.app_name);

                // Define the text of the forecast.
                String contentText = context.getString(R.string.notification_message);

                //build your notification here.
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(iconId).setTicker(contentText)
                                .setContentTitle(title)
                                .setContentText(contentText);
                mBuilder.setAutoCancel(true);

                String userRingtone = prefs.getString("notifications_new_message_ringtone", "SHOULD NOT HAPPEN");
                Uri notificationSound = Uri.parse(userRingtone);
                mBuilder.setSound(notificationSound);
                boolean shouldVibrate = prefs.getBoolean("notifications_new_message_vibrate", true);
                if(shouldVibrate) {
                    mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
                    //mBuilder.setVibrate(new long[] { 1000, 1000});
                }
                // Make something interesting happen when the user clicks on the notification.
                // In this case, opening the app is sufficient
                Intent resultIntent = new Intent(context, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                // MOVIE_NOTIFICATION_ID allows you to update the notification later on.
                mNotificationManager.notify(MOVIE_NOTIFICATION_ID, mBuilder.build());
                //refreshing last sync
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.commit();
                 }
            }
        }




    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

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

        movieDataParser.insertIntoDB(movieDataList, getContext());
        GridView gridView = (GridView) movieView;
        getContext().getContentResolver().notifyChange(MovieProviderGenerator.Movies.CONTENT_URI, null);
        notifyMovies();

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

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setIsSyncable(getSyncAccount(context), context.getString(R.string.content_authority), 1);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }





    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        String pw = accountManager.getPassword(newAccount);
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int syncFreqInDays = new Integer(prefs.getString("sync_frequency", "1"));
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL_DAY * syncFreqInDays, SYNC_FLEXTIME * syncFreqInDays);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
        //syncImmediately(context);
    }
}
