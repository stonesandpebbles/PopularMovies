package com.example.abhijeetsinghkgp.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieColumns;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private MovieAdapter moviesAdapter;
    private static final String MOVIE_DATA = "MovieData";
    public static final String MOVIE_DATA_BOOKMARKED = "MovieDataBookMarked";
    private static final int LANDSCAPE_COL = 3;
    private static final int POTRAIT_COL = 2;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final int LOADER_ID = 12345;
    private String queryOption = MovieColumns.POPULAR_SW;
    private BroadcastReceiver networkChangeReceiver;
    private boolean mTwoPane = false;
    public static final String TWO_PANE="twoPane";


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
        this.mTwoPane = ((MainActivity) getActivity()).mTwoPane;
    }

    public MainActivityFragment() {
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_rating) {
            queryOption = MovieColumns.TOP_RATED_SW;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            replaceFragmentWithEmpty();
            return true;
        }
        if(id == R.id.action_sort_popular){
            queryOption = MovieColumns.POPULAR_SW;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            replaceFragmentWithEmpty();
            return true;
        }

        if(id == R.id.action_sort_favourite){
            queryOption = MovieColumns.BOOKMARKED_SW;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            replaceFragmentWithEmpty();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragmentWithEmpty(){
        if(mTwoPane) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        networkChangeReceiver = new NetworkStateReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        moviesAdapter = new MovieAdapter(getActivity(), null, 0, mTwoPane);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(LANDSCAPE_COL);
        }
        else{
            gridView.setNumColumns(POTRAIT_COL);
        }
        gridView.setAdapter(moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Cursor cursor =  (Cursor)parent.getAdapter().getItem(position);
                MovieData movieData = new MovieData();
                if(cursor != null){
                    String movieId = cursor.getString(cursor.getColumnIndex(MovieColumns._ID));
                    movieData.setId(movieId);
                    movieData.setTitle(cursor.getString(cursor.getColumnIndex(MovieColumns.MOVIE_TITLE)));
                    movieData.setBackdropImageUrl(cursor.getString(cursor.getColumnIndex(MovieColumns.BACKDROP_IMG_URL)));
                    movieData.setPlot(cursor.getString(cursor.getColumnIndex(MovieColumns.MOVIE_PLOT)));
                    movieData.setPosterImageUrl(cursor.getString(cursor.getColumnIndex(MovieColumns.POSTER_IMG_URL)));
                    movieData.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieColumns.MOVIE_RELEASE)));
                    movieData.setRating(cursor.getString(cursor.getColumnIndex(MovieColumns.MOVIE_RATING)));

                    movieData.setBookMarkedSw(cursor.getString(cursor.getColumnIndex(MovieColumns.BOOKMARKED_SW)));
                    movieData.setPopularSw(cursor.getString(cursor.getColumnIndex(MovieColumns.POPULAR_SW)));
                    movieData.setTopRatedSw(cursor.getString(cursor.getColumnIndex(MovieColumns.TOP_RATED_SW)));

                }

                if(mTwoPane){
                    // In two-pane mode, show the detail view in this activity by
                    // adding or replacing the detail fragment using a
                    // fragment transaction.
                    Bundle args = new Bundle();
                    args.putParcelable(MOVIE_DATA, movieData);
                    args.putBoolean(TWO_PANE, mTwoPane);
                    DetailActivityFragment fragment = new DetailActivityFragment();
                    fragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_detail, fragment, DETAILFRAGMENT_TAG)
                            .commit();
                }
                else {
                    //Create intent
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(MOVIE_DATA, movieData);
                    intent.putExtra(TWO_PANE, mTwoPane);
                    //Start detail activity
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeReceiver, filter);
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       if(queryOption.equalsIgnoreCase(MovieColumns.POPULAR_SW)) {
           return new CursorLoader(getActivity(), MovieProviderGenerator.Movies.withPopular(FetchMoviesTask.YES),
                   null,
                   null,
                   null,
                   null);
       }
        else if(queryOption.equalsIgnoreCase(MovieColumns.TOP_RATED_SW)) {
           return new CursorLoader(getActivity(), MovieProviderGenerator.Movies.withTopRated(FetchMoviesTask.YES),
                   null,
                   null,
                   null,
                   null);
       }
        else {
           return new CursorLoader(getActivity(), MovieProviderGenerator.Movies.withBookMarked(FetchMoviesTask.YES),
                   null,
                   null,
                   null,
                   null);
       }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesAdapter.swapCursor(data);
        updateEmptyView();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }

    /*
     Updates the empty list view with contextually relevant information that the user can
     use to determine why they aren't seeing data.
  */
    private void updateEmptyView() {
        TextView tv = (TextView) getView().findViewById(R.id.gridview_empty);
        if (moviesAdapter.getCount() == 0 ) {
            if ( null != tv ) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_gridview;
                if (!isNetworkAvailable(getActivity()) ) {
                    message = R.string.geridview_empty_no_network;
                }
                else
                    message = R.string.fetching_data;
                tv.setText(message);
            }
        }
        else
            tv.setVisibility(View.GONE);
    }
}
