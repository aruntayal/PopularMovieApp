package com.sampleproj.arun.moviereview.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sampleproj.arun.moviereview.Data.MovieContract.MovieEntry;
import com.sampleproj.arun.moviereview.Data.MovieContract.TrailerEntry;
import com.sampleproj.arun.moviereview.Data.MovieContract.ReviewEntry;
import com.sampleproj.arun.moviereview.Data.MovieContract.FavouriteEntry;
/**
 * Created by arunt on 4/12/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER ," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY , " +
                MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_DESC + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + "  REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + "  REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT" + ")";

        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE = "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                FavouriteEntry._ID + " INTEGER ," +
                FavouriteEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY REFERENCES " + MovieEntry.TABLE_NAME + "(movie_id) " + " ON UPDATE CASCADE, " +
                FavouriteEntry.COLUMN_FAVOURITE_STATUS + " INTEGER NOT NULL " + ")";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                TrailerEntry._ID + " INTEGER ," +
                TrailerEntry.COLUMN_TRAILER_ID + " TEXT PRIMARY KEY , " +
                TrailerEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL " + ")";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ReviewEntry._ID + " INTEGER ," +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT PRIMARY KEY , " +
                ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR_NAME + " TEXT NOT NULL " + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

}
