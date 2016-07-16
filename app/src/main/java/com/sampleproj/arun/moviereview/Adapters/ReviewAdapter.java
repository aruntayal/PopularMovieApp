package com.sampleproj.arun.moviereview.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sampleproj.arun.moviereview.R;

/**
 * Created by arunt on 6/11/2016.
 */
public class ReviewAdapter   extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    final String LOG_TAG = "ARUN_" + ReviewAdapter.class.getSimpleName();

    final private Context mContext;
    private Cursor mCursor;
    public ReviewAdapter(Context context) {
        mContext = context;
    }


    /**
     * Cache of the children views for a Review list item.
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView author;
        public final TextView contentView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            author = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }

      /*
       This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
       RecyclerView that will be used to contain the view, so that it can get the current
       ItemSelectionManager from the view.
       One could implement this pattern without modifying RecyclerView by taking advantage
       of the view tag to store the ItemChoiceManager.
    */

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = R.layout.review_item_layout;

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ReviewAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
    }



    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String authorStr = mCursor.getString(mCursor.getColumnIndexOrThrow("author"));
        String reviewStr = mCursor.getString(mCursor.getColumnIndexOrThrow("review"));


        reviewAdapterViewHolder.author.setText(authorStr);
        reviewAdapterViewHolder.contentView.setText(reviewStr);
    }


    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

}