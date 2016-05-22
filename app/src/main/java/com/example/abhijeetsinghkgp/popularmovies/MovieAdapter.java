package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieColumns;
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
    private Context mContext;
    /**
     * Constructor
     *
     * @param context  The current context.
     */
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
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
        ImageView movieTile = (ImageView) view.findViewById(R.id.movie_image);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(IMAGE_BASE_URL)
                .appendPath(PATH_T)
                .appendPath(PATH_P)
                .appendPath(IMAGE_SIZE)
                .appendEncodedPath(cursor.getString(cursor.getColumnIndex(MovieColumns.POSTER_IMG_URL)));
        movieTile.setAdjustViewBounds(true);
        movieTile.setPadding(0, 0, 0, 0);
        Picasso.with(mContext).load(builder.build().toString()).into(movieTile);
    }
}
