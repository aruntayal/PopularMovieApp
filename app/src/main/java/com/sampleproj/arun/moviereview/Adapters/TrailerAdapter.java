
package com.sampleproj.arun.moviereview.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
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
public class TrailerAdapter extends CursorAdapter {
    private final String LOG_TAG = "ARUN_" + TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_trailer_layout, parent, false);

        return view;
    }

    private String getTrailerImageURL(Cursor cursor) {
        // get row indices for our cursor

        int count = cursor.getColumnCount();
        for (int i =0 ; i< count; i++) {
            String data = cursor.getString(i);
            String column_name = cursor.getColumnName(i);
            Log.e(LOG_TAG,"******" + column_name + "******" + data);
        }
      //  int idx_trailer_id = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_ID);
        int idx_mov_trailer_key = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY);
        String trailer_key = cursor.getString(idx_mov_trailer_key);
        String trailer_image_path = "http://img.youtube.com/vi/" + trailer_key + "/0.jpg";
        return trailer_image_path;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
       // trailer_item holder = new trailer_item();
       // holder = (trailer_item)view;

        String trailer_image = getTrailerImageURL(cursor);
        ImageView imgView = (ImageView)view.findViewById(R.id.grid_item_trailer_layout);
        Picasso.with(mContext).load(trailer_image).into(imgView);

    }

}


