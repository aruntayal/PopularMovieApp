package com.sampleproj.arun.moviereview.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sampleproj.arun.moviereview.Data.MovieContract;
import com.sampleproj.arun.moviereview.R;
import com.squareup.picasso.Picasso;

/**
 * Created by arunt on 4/16/2016.
 */
public class movieAdapter extends CursorAdapter {
    public movieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false);

        return view;
    }

    private String getImageURL(Cursor cursor) {
        // get row indices for our cursor

        int idx_mov_id = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int idx_mov_poster = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        String poster_url = cursor.getString(idx_mov_poster);
        String poster_path = "http://image.tmdb.org/t/p/w185/" + poster_url;
        return poster_path;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ImageView imgView = (ImageView) view.findViewById(R.id.grid_item_image);
        ImageView fav_img = (ImageView) view.findViewById(R.id.grid_fav_icon);

         String poster_path = getImageURL(cursor);

        int idx_mov_fav_status = cursor.getColumnIndex(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_STATUS);
         Integer is_fav = cursor.getInt(idx_mov_fav_status);
        if(is_fav > 0) {
            fav_img.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
        }
        else
        {
            fav_img.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
        }
        Picasso.with(mContext).load(poster_path).error(R.mipmap.ic_launcher).into(imgView);


    }


}
