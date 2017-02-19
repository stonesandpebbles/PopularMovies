package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieTrailerColumns;
import com.squareup.picasso.Picasso;

/**
 * Created by Abhijeet on 06-Jan-17.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {

    private static final String SCHEME = "http";
    private static final String IMAGE_BASE_URL = "img.youtube.com";
    private static final String VIDEO_BASE_URL = "www.youtube.com";
    private static final String PATH_VI = "vi";
    private static final String PATH_JPEG = "0.jpg";
    private static final String PATH_WATCH = "watch";
    private static final String QUERY_V = "v";
    Cursor dataCursor;
    Context mContext;
    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of  and
     *                .
     */
    public MovieTrailerAdapter(Context context, Cursor c, int flags) {
        this.dataCursor = c;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            this.view = v;
        }
    }


    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public MovieTrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override  instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieTrailerAdapter.ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);
        TextView movieTrailerTitle = (TextView) holder.view.findViewById(R.id.video_title);
        movieTrailerTitle.setText(dataCursor.getString(dataCursor.getColumnIndex(MovieTrailerColumns.TRAILER_NAME)));
        ImageView movieTrailerTile = (ImageView) holder.view.findViewById(R.id.trailer_icon);
        final Uri.Builder builder = new Uri.Builder();

        builder.scheme(SCHEME)
                .authority(IMAGE_BASE_URL)
                .appendPath(PATH_VI)
                .appendPath(dataCursor.getString(dataCursor.getColumnIndex(MovieTrailerColumns.TRAILER_URL)))
                .appendPath(PATH_JPEG);
        movieTrailerTile.setAdjustViewBounds(true);
        movieTrailerTile.setPadding(0, 0, 0, 0);
        Picasso.with(mContext).load(builder.build().toString()).into(movieTrailerTile);
        ImageButton movieTrailerPlayButton = (ImageButton) holder.view.findViewById(R.id.play_button);
        movieTrailerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri.Builder videoUriBuilder = new Uri.Builder();
                videoUriBuilder.scheme(SCHEME)
                        .authority(VIDEO_BASE_URL)
                        .appendPath(PATH_WATCH)
                        .appendQueryParameter(QUERY_V, dataCursor.getString(dataCursor.getColumnIndex(MovieTrailerColumns.TRAILER_URL)));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUriBuilder.toString()));
                mContext.startActivity(intent);
            }
        });
        //movieTrailerTitle.setMaxWidth(movieTrailerTile.getWidth());
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }
}
