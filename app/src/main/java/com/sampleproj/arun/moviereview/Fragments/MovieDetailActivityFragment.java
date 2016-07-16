package com.sampleproj.arun.moviereview.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sampleproj.arun.moviereview.R;
import com.sampleproj.arun.moviereview.adapters.ReviewAdapter;
import com.sampleproj.arun.moviereview.adapters.TrailerAdapter;
import com.sampleproj.arun.moviereview.asynctasks.FetchReviewsTask;
import com.sampleproj.arun.moviereview.asynctasks.FetchTrailersTask;
import com.sampleproj.arun.moviereview.asynctasks.ToggleFavouriteTask;
import com.sampleproj.arun.moviereview.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = "LOG_" + MovieDetailActivityFragment.class.getSimpleName();
    private final String YOU_TUBE_URL = "https://www.youtube.com/watch?v=";
    public static final String TAG = MovieDetailActivityFragment.class.getSimpleName();
    private String shareTrailer;
    public static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    private ImageView mBackDropImage;
    private TextView mOverView;
    private RecyclerView mReviewList;
    private GridView mTrailerGrid;
    private String mMovieStr;
    private Long mMovieId;
    private Uri mUri;
    private Long mIsFavourite;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private MenuItem is_fvourite;
    private TextView mRatingNum;
    private TextView mReleaseDate;
    View rootView;
    private View mReviewCardView ;
    private int mReviewPosition = RecyclerView.NO_POSITION;
    private static final int MOVIE_DETAIL_LOADER_ID = 1;
    private static final int MOVIE_REVIEW_LOADER_ID = 2;
    private static final int MOVIE_TRAILER_LOADER_ID = 3;

    public static final int COL_TRAILER_KEY = 3;

    ToggleFavouriteTask toggleFavTask;

    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
        shareTrailer = "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "OnCreateView Called");
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mBackDropImage = (ImageView) rootView.findViewById(R.id.movie_backdropImg);
        mOverView = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        mRatingNum = (TextView) rootView.findViewById(R.id.rating_num);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mReviewList = (RecyclerView) rootView.findViewById(R.id.movie_review_list);
        // Set the layout manager
        mReviewList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailerGrid = (GridView) rootView.findViewById(R.id.trailer_grid);
        mReviewCardView = (View)rootView.findViewById(R.id.reviewCard);
        mTrailerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String trailer_key = cursor.getString(COL_TRAILER_KEY);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer_key));
                startActivity(intent);
            }
        });

        mReviewAdapter = new ReviewAdapter(getActivity());
        mReviewList.setAdapter(mReviewAdapter);

        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mTrailerGrid.setAdapter(mTrailerAdapter);


        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailActivityFragment.DETAIL_MOVIE);
        } else {
            HideAllUIComponents();
        }
        if (mUri != null) {
            mMovieStr = mUri.toString();
            mMovieId = MovieContract.MovieEntry.getMovieFromUri(mMovieStr);
        } else {
            HideAllUIComponents();
        }
        mIsFavourite = 0L;
        return rootView;
    }

    public void HideAllUIComponents() {

        rootView.setVisibility(View.GONE);

    }

    public void updateReviews() {
        new FetchReviewsTask(getContext()).execute(Long.toString(mMovieId));
    }

    public void updateTrailers() {
        new FetchTrailersTask(getContext()).execute(Long.toString(mMovieId));
    }

    public void onStart() {
        super.onStart();
        if (mMovieStr != null) {

            updateReviews();
            updateTrailers();
        }
        ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, null, this);
        getLoaderManager().initLoader(MOVIE_REVIEW_LOADER_ID, null, this);
        getLoaderManager().initLoader(MOVIE_TRAILER_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);
        Log.e(LOG_TAG, "onCreateOptionsMenu");
        // Retrieve the share menu item
        MenuItem shareMenuItem = menu.findItem(R.id.action_share);
        is_fvourite = menu.findItem(R.id.action_favourite);
        if (mIsFavourite != 0) {
            is_fvourite.setIcon(R.drawable.ic_favorite_black_36dp);

        } else {
            is_fvourite.setIcon(R.drawable.ic_favorite_border_black_36dp);

        }
        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.e(LOG_TAG, "Share Action Provider is null?");
        }
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favourite:
                Log.e(LOG_TAG, "onOptionsItemSelected action_favourite");
                toggleFavTask = new ToggleFavouriteTask(getActivity(), is_fvourite);
                toggleFavTask.execute(mMovieId.toString(), mIsFavourite.toString());
                if (mIsFavourite == 0L)
                    mIsFavourite = 1L;
                else
                    mIsFavourite = 0L;

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                shareTrailer);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        if (mUri == null) {
            return null;
        }

        switch (id) {
            case MOVIE_DETAIL_LOADER_ID:
                // Now create and return a CursorLoader that will take care of
                // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        null,
                        null,
                        null,
                        null
                );

            case MOVIE_REVIEW_LOADER_ID: {

                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.buildReviewUri(Long.toString(mMovieId)),
                        null,
                        null,
                        null,
                        null
                );

            }
            case MOVIE_TRAILER_LOADER_ID: {
                Log.e(LOG_TAG, "MOVIE_TRAILER_LOADER_ID Created");
                return new CursorLoader(
                        getActivity(),
                        MovieContract.TrailerEntry.buildTrailerUri(Long.toString(mMovieId)),
                        null,
                        null,
                        null,
                        null
                );

            }
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(LOG_TAG, "OnLoadFinished");
        if (!cursor.moveToFirst()) {
            return;
        }

        switch (cursorLoader.getId()) {
            case MOVIE_REVIEW_LOADER_ID: {
                Log.e(LOG_TAG, "MOVIE_REVIEW_LOADER_ID");
                mReviewAdapter.swapCursor(cursor);
                if (mReviewPosition != RecyclerView.NO_POSITION) {
                    // If we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    mReviewList.smoothScrollToPosition(mReviewPosition);
                }
                Log.e("LOG_TAG", "MOVIE_REVIEW_LOADER_ID_ItemCount=" + Integer.toString(mReviewAdapter.getItemCount()));
                if(mReviewAdapter.getItemCount() == 0)
                {
                    mReviewCardView.setVisibility(View.GONE);
                }
                break;
            }
            case MOVIE_DETAIL_LOADER_ID: {
                String mov_poster_path = cursor.getString(MovieFragment.COL_MOVIE_POSTER);
                String mov_name = cursor.getString(MovieFragment.COL_MOVIE_NAME);
                String mov_desc = cursor.getString(MovieFragment.COL_MOVIE_DESC);
                Double mov_rating = cursor.getDouble(MovieFragment.COL_POPULARITY);

                mRatingNum.setText(Double.toString(mov_rating));
                mReleaseDate.setText(cursor.getString(MovieFragment.COL_MOVIE_RELEASEDATE));
                mMovieId = cursor.getLong(MovieFragment.COL_MOVIE_ID);
                int FvtColIdx = cursor.getColumnIndex("is_favourite");
                mIsFavourite = cursor.getLong(FvtColIdx);
                String mov_backdrop_path = cursor.getString(MovieFragment.COL_MOVIE_BACKDROP);


                ((AppCompatActivity) getActivity()).invalidateOptionsMenu();

                if (mov_name != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mov_name);
                }
                mOverView.setText(mov_desc);

                String backdrop_path = "http://image.tmdb.org/t/p/w500" + mov_backdrop_path;
                Picasso.with(getActivity()).load(backdrop_path).error(R.mipmap.ic_launcher).into(mBackDropImage);
                break;


            }
            case MOVIE_TRAILER_LOADER_ID: {
                Log.e(LOG_TAG, "MOVIE_TRAILER_LOADER_ID");

                int iter = 0;

                do {
                    iter++;
                    if (iter == 1) {
                        shareTrailer = YOU_TUBE_URL + cursor.getString(MovieDetailActivityFragment.COL_TRAILER_KEY);
                    }
                }
                while (cursor.moveToNext());
                cursor.moveToFirst();
                mTrailerAdapter.swapCursor(cursor);
                Log.e(LOG_TAG, "playTrailer    " + shareTrailer);


                break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(LOG_TAG, "MOVIE_TRAILER_LOADER_ID Reset");
        mTrailerAdapter.swapCursor(null);
        mReviewAdapter.swapCursor(null);
    }
}
