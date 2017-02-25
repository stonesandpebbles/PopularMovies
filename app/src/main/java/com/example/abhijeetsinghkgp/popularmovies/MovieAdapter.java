package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieColumns;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Abhijeet on 12-03-2016.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String SCHEME = "http";
    private static final String IMAGE_BASE_URL = "image.tmdb.org";
    private static final String IMAGE_SIZE = "w342";
    private static final String PATH_P = "p";
    private static final String PATH_T = "t";
    private final int mOrientation;
    private Context mContext;
    private boolean mTwoPane;
    private int height;
    private int width;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param mTwoPane
     */
    public  MovieAdapter(Context context, Cursor c, int flags, boolean mTwoPane) {
        super(context, c, flags);
        mContext = context;
        this.mTwoPane = mTwoPane;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        height = mContext.getResources().getDisplayMetrics().heightPixels;
        mOrientation = mContext.getResources().getConfiguration().orientation;

    }


    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return view;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //MovieData movieData = (MovieData) getItem(cursor.getPosition());
        final ImageView movieTile = (ImageView) view.findViewById(R.id.movie_image);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(IMAGE_BASE_URL)
                .appendPath(PATH_T)
                .appendPath(PATH_P)
                .appendPath(IMAGE_SIZE)
                .appendEncodedPath(cursor.getString(cursor.getColumnIndex(MovieColumns.POSTER_IMG_URL)));
        movieTile.setAdjustViewBounds(true);
        movieTile.setPadding(0, 0, 0, 0);
        int movieTileHeight = 0;
        int movieTileWidth = 0;
        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){
            if(mTwoPane) {
                movieTileHeight = height / 2;
                movieTileWidth = width/6;
            }
            else {
                movieTileHeight = 3 * height / 4;
                movieTileWidth = width/3;
            }
        }
        else{
            movieTileWidth = width/2;
            if(mTwoPane)
                movieTileHeight = height/3;
            else
                movieTileHeight = height/2;
        }
        final int finalMovieTileHeight = movieTileHeight;
        final int finalMovieTileWidth = movieTileWidth;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(movieTileWidth, movieTileHeight);
        movieTile.setLayoutParams(params);
        Picasso.with(mContext).load(builder.build().toString()).resize(movieTileWidth, movieTileHeight).into(movieTile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(mContext).load(R.drawable.ic_broken_image_black_24dp).
                        into(movieTile);
            }
        });
    }
}
