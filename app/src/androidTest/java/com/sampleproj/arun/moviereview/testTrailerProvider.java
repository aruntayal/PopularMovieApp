package com.sampleproj.arun.moviereview;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.sampleproj.arun.moviereview.Data.MovieContract;
import com.sampleproj.arun.moviereview.Data.MovieDbHelper;

/**
 * Created by arunt on 6/9/2016.
 */
public class testTrailerProvider extends AndroidTestCase {

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(MovieContract.TrailerEntry.CONTENT_URI);
        Log.e("ARUNTT",type);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                MovieContract.TrailerEntry.CONTENT_DIR_TYPE, type);
        String testMovie = "94074";
        String testtrailer = "94076";
        // content://com.example.android.sunshine.app/weather/94074
        Uri tmpUri = MovieContract.TrailerEntry.buildTrailerUri(testMovie,testtrailer);
        Log.e("ARUNTT",tmpUri.toString());
        type = mContext.getContentResolver().getType(
                MovieContract.TrailerEntry.buildTrailerUri(testMovie,testtrailer));

        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
                MovieContract.TrailerEntry.CONTENT_ITEM_TYPE, type);
    }


    public void testBasicMovieQuery()
    {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTrailerValues("1234","43331","key1");
        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert TrailerEntry into the Database", trailerRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", trailerCursor, testValues);
    }
}
