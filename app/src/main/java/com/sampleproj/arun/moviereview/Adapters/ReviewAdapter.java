package com.sampleproj.arun.moviereview.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sampleproj.arun.moviereview.R;

/**
 * Created by arunt on 6/11/2016.
 */
public class ReviewAdapter extends CursorAdapter {
    final String LOG_TAG = "ARUN_" + ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return LayoutInflater.from(context).inflate(R.layout.review_item_layout, parent, false);
        View view = LayoutInflater.from(context).inflate(R.layout.review_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template

        ViewHolder viewHolder = (ViewHolder) view.getTag();
       // TextView author = (TextView) view.findViewById(R.id.review_author);
        //TextView content = (TextView) view.findViewById(R.id.review_content);
        // Extract properties from cursor
        String authorStr = cursor.getString(cursor.getColumnIndexOrThrow("author"));
        String reviewStr = cursor.getString(cursor.getColumnIndexOrThrow("review"));

        viewHolder.author.setText(authorStr);
        viewHolder.contentView.setText(reviewStr);

        Log.e(LOG_TAG, "authos is : " + authorStr);
        Log.e(LOG_TAG, "review is : " + reviewStr);
        // Populate fields with extracted properties
       // author.setText(authorStr);
        //content.setText(reviewStr);
        //Log.e(LOG_TAG, "Bind View Complete" + "#####Author###" + authorStr + "#####review###" + reviewStr  );
       // notifyDataSetChanged();
    }


    public static class ViewHolder {

        public final TextView author;
        public final TextView contentView;

        public ViewHolder(View view) {
            author = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);

        }
    }
}