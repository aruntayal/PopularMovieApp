package com.sampleproj.arun.moviereview.Sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sampleproj.arun.moviereview.Data.MovieContract;
import com.sampleproj.arun.moviereview.R;
import com.sampleproj.arun.moviereview.config;

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


public class PopularMovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = "ARUN_" + PopularMovieSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public PopularMovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

        // If there's no zip code, there's nothing to look up.  Verify size of params.
       /* if (params.length == 0) {
            return null;
        }*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sce = prefs.getString("sort_movie", "Most Popular");
        // String sce = intent.getStringExtra(SORT_CRITERIA_EXTRA);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

            /*String format = "json";
            String units = "metric";
            int numDays = 7;*/

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //
            final String POP_MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/popular?";
            final String TOP_RATED_MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/top_rated?";

            final String API_KEY = "api_key";
            Uri builtUri;
            //Log.e("ARUNXXXX",params[0]);
            if (sce.equals("Most Popular")) {
                builtUri = Uri.parse(POP_MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, config.API_KEY)
                        .build();
            } else {
                builtUri = Uri.parse(TOP_RATED_MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, config.API_KEY)
                        .build();
            }

            URL url = new URL(builtUri.toString());
            Log.d("ARUNurl", url.toString());
            // Create the request to Moviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
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
                return;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in receiving data", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
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
            Log.e("ARUN", movieJsonStr);
            getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return;
    }
    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {


        final String TMD_RESULT  = "results";
        final String TMD_POSTER_PATH  = "poster_path";
        final String TMD_ID  = "id";
        final String TMD_OVERVIEW  = "overview";
        final String TMD_TITLE = "title";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_BACKDROPPATH = "backdrop_path";
        final String TMD_POPULARITY = "popularity";
        final String TMD_VOTE_AVG = "vote_average";
        final String TMD_FAVOURITE = "is_favourite";

        // final String TMD_RELEASE_DATE = "release_date";
        //final String TMD_RELEASE_DATE = "release_date";
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMD_RESULT);


         String[] resultStrs = new String[movieArray.length()];
        Log.e(LOG_TAG, "JSONARRAY length is " + Integer.toString(movieArray.length()));;
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
        Vector<ContentValues> favMovieVector = new Vector<ContentValues>(movieArray.length());
        for(int i = 0; i < movieArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject movieObj = movieArray.getJSONObject(i);
            Log.e(LOG_TAG,"movieObj is: " + movieObj.toString());
            String poster_path = movieObj.getString(TMD_POSTER_PATH);
            int id = movieObj.getInt(TMD_ID);
            String overview = movieObj.getString(TMD_OVERVIEW);
            String title = movieObj.getString(TMD_TITLE);
            String date = movieObj.getString(TMD_RELEASE_DATE);
            Double vote_avg = movieObj.getDouble(TMD_VOTE_AVG);
            Double popularity = movieObj.getDouble(TMD_POPULARITY);
            String backdrop_path = movieObj.getString(TMD_BACKDROPPATH);
         //   Log.e(LOG_TAG,"#####" + vote_avg.toString() + "#####" + popularity.toString() + "#####" + backdrop_path + "#####");

            // highAndLow = formatHighLows(high, low);
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,poster_path);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DESC,overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME,title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,date);
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,backdrop_path);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_avg);
        //    resultStrs[i] = String.valueOf(id) + "#####" + backdrop_path + "#####" + String.valueOf(popularity) + "#####" + String.valueOf(vote_avg);
            Log.e(LOG_TAG,"movieVAlues is: " + movieValues.toString());
            cVVector.add(movieValues);
            ContentValues favMovieValues = new ContentValues();
            favMovieValues.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID,id);
            favMovieValues.put(MovieContract.FavouriteEntry.COLUMN_FAVOURITE_STATUS,0);
            favMovieVector.add(favMovieValues);
        }
       // Log.e("ARUN123456", resultStrs.toString());
        //return resultStrs;

        int inserted = 0;
        // add to database
        //Log.e(LOG_TAG,String. (cVVector.size()).;
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        if ( favMovieVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[favMovieVector.size()];
            favMovieVector.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(MovieContract.FavouriteEntry.CONTENT_URI, cvArray);
        }
    }



    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
