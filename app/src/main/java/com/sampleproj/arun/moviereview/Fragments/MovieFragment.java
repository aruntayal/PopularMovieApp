package com.sampleproj.arun.moviereview.fragments;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.sampleproj.arun.moviereview.adapters.movieAdapter;
import com.sampleproj.arun.moviereview.data.MovieContract;
import com.sampleproj.arun.moviereview.sync.PopularMovieSyncAdapter;
import com.sampleproj.arun.moviereview.R;

import java.util.ArrayList;

//import android.database.Cursor;


public class MovieFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MOVIE_LOADER_ID = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private movieAdapter mImageAdapter;
    private ArrayList<String> mGridData;
    private GridView mGridView;
    //private OnFragmentInteractionListener mListener;


    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_NAME = 2;
    public static final int COL_MOVIE_DESC = 3;
    public static final int COL_MOVIE_POSTER = 4;
    public static final int COL_MOVIE_BACKDROP = 5;
    public static final int COL_POPULARITY = 7;
    public static final int COL_MOVIE_RELEASEDATE = 8;
    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mImageAdapter = new movieAdapter(getActivity(), null, 0);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        mGridData = new ArrayList<>();
        mGridView = (GridView)rootView.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Toast toast = Toast.makeText(getContext(), Integer.toString(position),  Toast.LENGTH_SHORT);
                toast.show();
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                long mov_id = cursor.getLong(COL_MOVIE_ID);
                String mov_poster_path = cursor.getString(COL_MOVIE_POSTER);
                String mov_name = cursor.getString(COL_MOVIE_NAME);
                String mov_desc = cursor.getString(COL_MOVIE_DESC);
                //Uri movieDetailUri = MovieContract.MovieEntry.buildMovieDetailUri(mov_id, mov_name, mov_desc, mov_poster_path);

               Uri movieDetailUri = MovieContract.MovieEntry.buildMoviesUri(mov_id);
                Log.e("ARUNTEST", movieDetailUri.toString());
                /*Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .setData(movieDetailUri);
                startActivity(intent);*/
                ((Callback) getActivity()).onItemSelected(movieDetailUri);
            }
          }
        );
        mGridView.setAdapter(mImageAdapter);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    public void updateMovie()
    {

        PopularMovieSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.popmoviefragment, menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_type = prefs.getString("sort_movie", "Most Popular");
        String sortOrder = null;
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        if( sort_type.equals("Favourite"))
        {
            return new CursorLoader(getActivity(),
                    movieUri,
                    null,
                    MovieContract.FavouriteEntry.COLUMN_FAVOURITE_STATUS + " = ?",
                    new String[]{"1"},
                    null);
        }
        else
        {
            if(sort_type.equals("Most Popular"))  {
                 sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            }
            else
            {
                  sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
            }

        }
        Log.d(LOG_TAG,"Inside OnCreateLoader with sort order as while sort_type was : " +sortOrder + " " + sort_type);



        return new CursorLoader(getActivity(),
                movieUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        cursor.moveToFirst();
        int iCount = 0;
        while (cursor.isAfterLast() == false)
        {
            iCount++;
            cursor.moveToNext();
        }
        Log.e("ARUN456", Integer.toString(iCount));
        mImageAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mImageAdapter.swapCursor(null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


}
