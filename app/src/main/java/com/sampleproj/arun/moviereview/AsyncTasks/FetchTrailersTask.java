package com.sampleproj.arun.moviereview.asynctasks;

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
public class FetchTrailersTask extends AsyncTask<String, Void, String[]> {

    private final Context mContext;
    private final String LOG_TAG = "LOG_" + FetchTrailersTask.class.getSimpleName();
    public FetchTrailersTask(Context context) {
        mContext = context;
    }


    private void getTrailerDataFromJson(String movie_id, String TrailerJsonStr)
            throws JSONException {
        final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        final String TMD_RESULT = "results";
        final String TMD_KEY = "key";
        final String TMD_ID = "id";

        JSONObject movieJson = new JSONObject(TrailerJsonStr);
        JSONArray TrailerArray = movieJson.getJSONArray(TMD_RESULT);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(TrailerArray.length());

        for(int i = 0; i < TrailerArray.length(); i++) {

            // Get the JSON object representing the trailer
            JSONObject TrailerObj = TrailerArray.getJSONObject(i);

            String trailer_id = TrailerObj.getString(TMD_ID);

            String key = TrailerObj.getString(TMD_KEY);

            ContentValues trailerValues = new ContentValues();
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie_id);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer_id);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, key);

            cVVector.add(trailerValues);

            int inserted = 0;
            // add to database
            //Log.e(LOG_TAG,String. (cVVector.size()).;
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
                Log.e(LOG_TAG,"inserted c: " + Integer.toString(inserted));
            }
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
            final String POP_MOVIE_TRAILER_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
            final String API_KEY = "api_key";
            Uri builtUri = Uri.parse(POP_MOVIE_TRAILER_URL).buildUpon()
                    .appendQueryParameter(API_KEY, "b7df6d788a013a743ab19316f13c0b09")
                    .build();
            URL url = new URL(builtUri.toString());
            Log.e(LOG_TAG,url.toString());
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
            // If the code didn't successfully get the weather data, there's no point in attemping
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
            Log.e(LOG_TAG,movieJsonStr);
            getTrailerDataFromJson(params[0], movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }



}
