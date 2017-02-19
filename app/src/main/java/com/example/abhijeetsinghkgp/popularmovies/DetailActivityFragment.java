package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String SCHEME = "http";
    private static final String IMAGE_BASE_URL = "image.tmdb.org";
    private static final String IMAGE_SIZE = "w342";
    private static final String MOVIE_DATA = "MovieData";
    private static final int TRAILER_LOADER_ID = 678910;
    private static final int REVIEW_LOADER_ID = 96321;
    private MovieData movieData= null;
    private  ViewPager reviewsPager;
    private YouTubePlayer YPlayer;

    private MovieTrailerAdapter movieTrailerAdapter;
    private MovieReviewAdapter movieReviewAdapter;
    private RecyclerView.LayoutManager mTrailerLayoutManager;
    private RecyclerView.LayoutManager mReviewLayoutManager;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent movieDetail = getActivity().getIntent();
        movieData = movieDetail.getParcelableExtra(MOVIE_DATA);
        TextView movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
        movieTitle.setText(movieData.getTitle());
        ImageView movieIcon = (ImageView)rootView.findViewById(R.id.movie_icon);
        ImageButton starIcon = (ImageButton)rootView.findViewById(R.id.star_icon);
        int starBackgroundColor = movieData.getBookMarkedSw().equalsIgnoreCase(FetchMoviesTask.YES)? R.color.yellow : R.color.grey;
        starIcon.setBackgroundColor(getResources().getColor(starBackgroundColor));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(IMAGE_BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath(IMAGE_SIZE)
                .appendEncodedPath(movieData.getPosterImageUrl());

        Picasso.with(getActivity()).load(builder.build().toString()).into(movieIcon);
        //TextView movieTime = (TextView)rootView.findViewById(R.id.movie_time);
        //movieTime.setText(movieData.get);

        TextView movieReleaseYear = (TextView)rootView.findViewById(R.id.movie_release_year);
        DateFormat releaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = null;
        try {
            releaseDate = releaseDateFormat.parse(movieData.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar date = new GregorianCalendar();
        date.setTime(releaseDate);
        Integer year = date.get(Calendar.YEAR);
        movieReleaseYear.setText(year.toString());
        TextView movieRating = (TextView)rootView.findViewById(R.id.movie_rating);
        movieRating.setText(movieData.getRating() + "/10");
        TextView moviePlot = (TextView)rootView.findViewById(R.id.movie_plot);
        moviePlot.setText(movieData.getPlot());
        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), null, 0);
        //movieReviewAdapter = new MovieReviewAdapter(getActivity(), null, 0);
        RecyclerView trailers = (RecyclerView) rootView.findViewById(R.id.trailer_view);
        //RecyclerView reviews = (RecyclerView) rootView.findViewById(R.id.review_view);
        reviewsPager = (ViewPager) rootView.findViewById(R.id.review_pager);
        movieReviewAdapter = new MovieReviewAdapter(getActivity(), null, getChildFragmentManager());
        reviewsPager.setAdapter(movieReviewAdapter);

        mTrailerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailers.setLayoutManager(mTrailerLayoutManager);
        trailers.setAdapter(movieTrailerAdapter);
        //mReviewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //reviews.setLayoutManager(mReviewLayoutManager);
        //reviews.setAdapter(movieReviewAdapter);

        return rootView;
    }

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
        getLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
/*        reviewsPager = (ViewPager) getView().findViewById(R.id.review_pager);
        movieReviewAdapter = new MovieReviewAdapter(getActivity(), null, getActivity().getSupportFragmentManager());
        reviewsPager.setAdapter(movieReviewAdapter);*/
        super.onActivityCreated(savedInstanceState);
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
        CursorLoader loader = null;
        if(id == TRAILER_LOADER_ID) {
            loader = new  CursorLoader(getActivity(), MovieProviderGenerator.MovieTrailers.withId(movieData.getId()),
                    null,
                    null,
                    null,
                    null);
        }
        else if(id == REVIEW_LOADER_ID) {
            loader =  new CursorLoader(getActivity(), MovieProviderGenerator.MovieReviews.withId(movieData.getId()),
                    null,
                    null,
                    null,
                    null);
        }
        return loader;
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
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
        if(loader.getId() == TRAILER_LOADER_ID)
            movieTrailerAdapter.swapCursor(data);
        else if(loader.getId() == REVIEW_LOADER_ID)
            movieReviewAdapter.swapCursor(data);
        //String s = DatabaseUtils.dumpCursorToString(data);
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
        if(loader.getId() == TRAILER_LOADER_ID)
            movieTrailerAdapter.swapCursor(null);
        else if(loader.getId() == REVIEW_LOADER_ID)
            movieReviewAdapter.swapCursor(null);
    }
}
