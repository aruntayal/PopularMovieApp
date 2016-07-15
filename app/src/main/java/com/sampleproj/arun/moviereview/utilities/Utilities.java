package com.sampleproj.arun.moviereview.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.sampleproj.arun.moviereview.R;
import com.sampleproj.arun.moviereview.sync.PopularMovieSyncAdapter;

/**
 * Created by arunt on 7/15/2016.
 */

public class Utilities {

    static public boolean isNetworkAvailable(Context c)
    {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /* @param c Context used to get the SharedPreferences
    * @return the location status integer type
    */
    @SuppressWarnings("ResourceType")
    static public @PopularMovieSyncAdapter.MovieStatus
    int getMovieStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_pop_movie_status_key), PopularMovieSyncAdapter.MOVIE_STATUS_UNKNOWN);
    }
}
