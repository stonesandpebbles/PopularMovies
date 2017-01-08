package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieColumns;
import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private MovieAdapter moviesAdapter;
    private static final String MOVIE_DATA = "MovieData";
    private static final String MOVIE_DATA_BOOKMARKED = "MovieDataBookMarked";
    private static final int LANDSCAPE_COL = 3;
    private static final int POTRAIT_COL = 2;
    private static final int LOADER_ID = 12345;
    private String queryOption = MovieColumns.POPULAR_SW;


    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public MainActivityFragment() {
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_rating) {
            queryOption = MovieColumns.TOP_RATED_SW;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            return true;
        }
        if(id == R.id.action_sort_popular){
            queryOption = MovieColumns.POPULAR_SW;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            return true;
        }

        if(id == R.id.action_sort_favourite){
            queryOption = MovieColumns.BOOKMARKED_SW;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        GridView gridView = (GridView)getView();
//        ArrayList<MovieData> movieAdapterData = new ArrayList<>();
//        MovieAdapter movieAdapter = (MovieAdapter)gridView.getAdapter();
//        int numMovies = movieAdapter.getCount();
//        for(int i = 0; i < numMovies; i++){
//            //movieAdapterData.add(movieAdapter.getItem(i).);
//        }
//        outState.putParcelableArrayList("Movies", movieAdapterData);
        super.onSaveInstanceState(outState);
    }



    /**
     * Called to do initial creation of a fragment.  This is called after

     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
//            movieDataList  = new ArrayList<>();
//        }
//        else {
//            movieDataList = savedInstanceState.getParcelableArrayList("Movies");
//        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        moviesAdapter = new MovieAdapter(getActivity(), null, 0);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(LANDSCAPE_COL);
        }
        else{
            gridView.setNumColumns(POTRAIT_COL);
        }
        //if(!movieDataList.isEmpty()){
            gridView.setAdapter(moviesAdapter);

       // }

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
                if(getActivity().getIntent().hasExtra(MOVIE_DATA_BOOKMARKED)){
                    MovieData movieDataIfBookMarked = getActivity().getIntent().getParcelableExtra(MOVIE_DATA_BOOKMARKED);
                    if(movieDataIfBookMarked.getId().equalsIgnoreCase(movieData.getId()))
                        movieData.setBookMarkedSw(movieDataIfBookMarked.getBookMarkedSw());
                }
                //Create intent
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(MOVIE_DATA, movieData);
                //Start detail activity
                startActivity(intent);
            }
        });
        //new FetchMoviesTask(getView(), getActivity()).execute();
        return rootView;
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
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

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     * <p/>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }
}
