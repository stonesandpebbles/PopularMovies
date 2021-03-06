package com.example.abhijeetsinghkgp.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;

import com.example.abhijeetsinghkgp.popularmovies.data.MovieReviewColumns;

/**
 * Created by Abhijeet on 06-Jan-17.
 */

public class MovieReviewAdapter extends FragmentPagerAdapter {

    Cursor dataCursor;
    Context mContext;
    LayoutInflater mLayoutInflater;


    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        dataCursor.moveToPosition(position);
        String author = dataCursor.getString(dataCursor.getColumnIndex(MovieReviewColumns.AUTHOR));
        String content = dataCursor.getString(dataCursor.getColumnIndex(MovieReviewColumns.CONTENT));
        ReviewData reviewData = new ReviewData();
        reviewData.setAuthor(author);
        reviewData.setContent(content);
        //return new ReviewFragment();
        return ReviewFragment.newInstance(reviewData);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        //return 10;
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    public MovieReviewAdapter(Context context, Cursor c, FragmentManager fm) {
        super(fm);
        this.dataCursor = c;
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if(cursor == null){
            //mContext.getActivity().setContentView(R.layout.review_empty);
        }
        return oldCursor;
    }
}
