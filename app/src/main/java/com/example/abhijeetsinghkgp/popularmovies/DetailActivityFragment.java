package com.example.abhijeetsinghkgp.popularmovies;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieProviderGenerator;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Callback;
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
    private String REVIEW_INDEX = "REVIEW_INDEX";
    private int mReviewIndex = 0;
    private YouTubePlayer YPlayer;

    private MovieTrailerAdapter movieTrailerAdapter;
    private MovieReviewAdapter movieReviewAdapter;
    private RecyclerView.LayoutManager mTrailerLayoutManager;
    private RecyclerView.LayoutManager mReviewLayoutManager;
    private boolean mIsRestoredFromBackground = true;

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
        final ImageView movieIcon = (ImageView)rootView.findViewById(R.id.movie_icon);
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

        Picasso.with(getActivity()).load(builder.build().toString()).into(movieIcon, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getActivity()).load(R.drawable.ic_broken_image_black_24dp).resize(300, 300).centerInside().into(movieIcon);
            }
        });
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
        movieRating.setText(movieData.getRating() + getString(R.string.max_rating));
        TextView moviePlot = (TextView)rootView.findViewById(R.id.movie_plot);
        moviePlot.setText(movieData.getPlot());
        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), null, 0);
        //movieReviewAdapter = new MovieReviewAdapter(getActivity(), null, 0);
        RecyclerView trailers = (RecyclerView) rootView.findViewById(R.id.trailer_view);
        //RecyclerView reviews = (RecyclerView) rootView.findViewById(R.id.review_view);
        reviewsPager = (ViewPager) rootView.findViewById(R.id.review_pager);
        movieReviewAdapter = new MovieReviewAdapter(getActivity(), null, getChildFragmentManager());
        reviewsPager.setAdapter(movieReviewAdapter);
        mIsRestoredFromBackground = false;
        mTrailerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        trailers.setLayoutManager(mTrailerLayoutManager);
        trailers.setAdapter(movieTrailerAdapter);

        //mReviewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //reviews.setLayoutManager(mReviewLayoutManager);
        //reviews.setAdapter(movieReviewAdapter);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
/*        reviewsPager = (ViewPager) getView().findViewById(R.id.review_pager);
        movieReviewAdapter = new MovieReviewAdapter(getActivity(), null, getActivity().getSupportFragmentManager());
        reviewsPager.setAdapter(movieReviewAdapter);*/
        super.onActivityCreated(savedInstanceState);
    }


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


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == TRAILER_LOADER_ID) {
            movieTrailerAdapter.swapCursor(data);
            if(data.getCount() == 0)
                showEmptyViewTrailer();
        }
        else if(loader.getId() == REVIEW_LOADER_ID) {
            movieReviewAdapter.swapCursor(data);
            if(data.getCount() == 0)
                showEmptyViewReview();
        }
        //String s = DatabaseUtils.dumpCursorToString(data);
    }

    private void showEmptyViewReview() {
        TextView emptyReview = (TextView) getView().findViewById(R.id.review_empty);
        ViewPager review = (ViewPager) getView().findViewById(R.id.review_pager);
        emptyReview.setVisibility(View.VISIBLE);
        review.setVisibility(View.GONE);
    }

    private void showEmptyViewTrailer() {
        TextView emptyTrailer = (TextView) getView().findViewById(R.id.trailer_empty);
        RecyclerView trailers = (RecyclerView) getView().findViewById(R.id.trailer_view);
        emptyTrailer.setVisibility(View.VISIBLE);
        trailers.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(REVIEW_INDEX, reviewsPager.getCurrentItem());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            mReviewIndex = savedInstanceState.getInt(REVIEW_INDEX);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsRestoredFromBackground = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mIsRestoredFromBackground)
            reviewsPager.setCurrentItem(mReviewIndex);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == TRAILER_LOADER_ID)
            movieTrailerAdapter.swapCursor(null);
        else if(loader.getId() == REVIEW_LOADER_ID)
            movieReviewAdapter.swapCursor(null);
    }
}
