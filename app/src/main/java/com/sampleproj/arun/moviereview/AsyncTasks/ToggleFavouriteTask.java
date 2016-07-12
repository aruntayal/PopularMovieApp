package com.sampleproj.arun.moviereview.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;

import com.sampleproj.arun.moviereview.data.MovieContract;
import com.sampleproj.arun.moviereview.R;

/**
 * Created by arunt on 6/26/2016.
 */
public class ToggleFavouriteTask extends AsyncTask<String, Void, Integer> {

    private final String LOG_TAG = "LOG_" + ToggleFavouriteTask.class.getSimpleName();


    private final Context mContext;
    private final MenuItem mMenuItem;

    public ToggleFavouriteTask(Context context, MenuItem mt) {
        mContext = context;
        mMenuItem = mt;
    }

    @Override
    protected Integer doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        String mov_id = params[0];
        String is_fav = params[1];
        Integer mIsFavourite = Integer.valueOf(is_fav);
        ContentValues favValue = new ContentValues();
        favValue.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID, Integer.valueOf(mov_id));

        if(mIsFavourite == 0)
            favValue.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_STATUS,1);
        else
            favValue.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_STATUS, 0);

        Uri favUri = MovieContract.FavouriteEntry.buildFavouriteUri(mov_id);
        Integer retVal = mContext.getContentResolver().update(
                favUri, favValue,
                null,
                null);
        Log.e(LOG_TAG, "Update favourite status " + retVal.toString());

        return mIsFavourite;
    }

    @Override
    protected void onPostExecute(Integer mIsFavourite) {

        if ((mIsFavourite != 0)) { // it is fav we have to uncheck it
            mMenuItem.setIcon(R.drawable.ic_favorite_border_black_36dp);


        } else { // it is non fav we have to check it
            if ((mIsFavourite == 0)) {
                mMenuItem.setIcon(R.drawable.ic_favorite_black_36dp);

            }

        }
    }


    }
