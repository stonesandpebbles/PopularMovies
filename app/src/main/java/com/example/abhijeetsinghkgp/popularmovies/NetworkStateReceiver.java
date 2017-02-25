package com.example.abhijeetsinghkgp.popularmovies;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Abhijeet on 23-Feb-17.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        if(MainActivityFragment.isNetworkAvailable(context)){
            TextView gridEmptyView = (TextView) ((Activity)context).findViewById(R.id.gridview_empty);
            gridEmptyView.setText(R.string.fetching_data);
        }

    }
}
