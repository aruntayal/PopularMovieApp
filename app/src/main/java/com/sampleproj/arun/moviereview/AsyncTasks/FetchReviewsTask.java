package com.sampleproj.arun.moviereview.asynctasks;

/**
 * Created by arunt on 6/10/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sampleproj.arun.moviereview.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by arunt on 6/9/2016.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, String[]> {

    private final Context mContext;
    private final String LOG_TAG = "LOG_" + FetchReviewsTask.class.getSimpleName();

    public FetchReviewsTask(Context context) {
        mContext = context;
    }


    private void getReviewDataFromJson(String movie_id, String ReviewJsonStr)
            throws JSONException {
        final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        final String TMD_RESULT = "results";
        final String TMD_REVIEW = "content";
        final String TMD_REVIEW_ID = "id";
        final String TMD_AUTHOR_NAME = "author";
        JSONObject reviewJSON = new JSONObject(ReviewJsonStr);
        JSONArray reviewArray = reviewJSON.getJSONArray(TMD_RESULT);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

        for (int i = 0; i < reviewArray.length(); i++) {


            JSONObject TrailerObj = reviewArray.getJSONObject(i);

            String review_id = TrailerObj.getString(TMD_REVIEW_ID);

            String review = TrailerObj.getString(TMD_REVIEW);
            String name = TrailerObj.getString(TMD_AUTHOR_NAME);


            if ((review_id != "") && (review != "") && name != "") {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie_id);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review_id);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW, review);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME, name);

                cVVector.add(reviewValues);
            }
        }

        int inserted = 0;

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            Log.d(LOG_TAG, "review inserted rows: " + Integer.toString(inserted));

        }

    }

    protected String[] doInBackground(String... params) {


        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;


        try {
            final String POP_MOVIE_REVIEW_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
            final String API_KEY = "api_key";
            Uri builtUri = Uri.parse(POP_MOVIE_REVIEW_URL).buildUpon()
                    .appendQueryParameter(API_KEY, "b7df6d788a013a743ab19316f13c0b09")
                    .build();
            URL url = new URL(builtUri.toString());
            Log.e(LOG_TAG, url.toString());
            // Create the request to Moviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in receiving data", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            Log.e(LOG_TAG, movieJsonStr);
            getReviewDataFromJson(params[0], movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }


}
