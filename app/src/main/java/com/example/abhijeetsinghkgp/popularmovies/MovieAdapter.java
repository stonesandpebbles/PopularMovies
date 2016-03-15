package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Abhijeet on 12-03-2016.
 */
public class MovieAdapter extends ArrayAdapter<MovieData>{
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
     * @param movieDataList  The objects to represent in the GridView.
     */
    public MovieAdapter(Context context, List<MovieData> movieDataList) {
        super(context, 0, movieDataList);
        mContext = context;
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieData movieData = (MovieData) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);
        }
        ImageView movieTile = (ImageView) convertView.findViewById(R.id.movie_image);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(IMAGE_BASE_URL)
                .appendPath(PATH_T)
                .appendPath(PATH_P)
                .appendPath(IMAGE_SIZE)
                .appendEncodedPath(movieData.getPosterImageUrl());
        movieTile.setAdjustViewBounds(true);
        movieTile.setPadding(0, 0, 0, 0);
        Picasso.with(mContext).load(builder.build().toString()).into(movieTile);
        return convertView;
    }
}
