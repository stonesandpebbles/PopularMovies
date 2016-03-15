package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MovieAdapter moviesAdapter;
    private static final String MOVIE_DATA = "MovieData";
    private static final String POPULAR = "popular?";
    private static final String HIGHEST_RATED = "top_rated?";
    private static final int LANDSCAPE_COL = 3;
    private static final int POTRAIT_COL = 2;
    private List<MovieData> movieDataList;
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
            new FetchMoviesTask(getView(), getActivity()).execute(HIGHEST_RATED);
            return true;
        }
        if(id == R.id.action_sort_popular){
            new FetchMoviesTask(getView(), getActivity()).execute(POPULAR);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        GridView gridView = (GridView)getView();
        ArrayList<MovieData> movieAdapterData = new ArrayList<>();
        MovieAdapter movieAdapter = (MovieAdapter)gridView.getAdapter();
        int numMovies = movieAdapter.getCount();
        for(int i = 0; i < numMovies; i++){
            movieAdapterData.add(movieAdapter.getItem(i));
        }
        outState.putParcelableArrayList("Movies", movieAdapterData);
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
        if(savedInstanceState == null || !savedInstanceState.containsKey("Movies")) {
            movieDataList  = new ArrayList<>();
        }
        else {
            movieDataList = savedInstanceState.getParcelableArrayList("Movies");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(LANDSCAPE_COL);
        }
        else{
            gridView.setNumColumns(POTRAIT_COL);
        }
        if(!movieDataList.isEmpty()){
            gridView.setAdapter(new MovieAdapter(getActivity(), movieDataList));
        }
        else {
            new FetchMoviesTask(rootView, getActivity()).execute(POPULAR);
        }
        //moviesAdapter = new MovieAdapter(getActivity(), movieDataList);

       //gridView.setAdapter(moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieData movieData = (MovieData) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(MOVIE_DATA, movieData);
                //Start detail activity
                startActivity(intent);
            }
        });
        return rootView;
    }
}
