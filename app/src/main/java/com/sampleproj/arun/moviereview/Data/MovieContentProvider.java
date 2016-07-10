package com.sampleproj.arun.moviereview.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.database.DatabaseUtils;

/**
 * Created by arunt on 4/16/2016.
 */
public class MovieContentProvider extends ContentProvider {

    private static final String LOG_TAG = MovieContentProvider.class.getSimpleName();
    // Codes For UriMatcher
    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILERS = 102;
    static final int MOVIE_TRAILERS = 103;
    static final int MOVIE_TRAILER_WITH_ID = 104;
    static final int REVIEWS = 105;
    static final int MOVIE_REVIEWS = 106;
    static final int MOVIE_REVIEW_WITH_ID = 107;
    static final int MOVIE_FAVOURITE = 108;
    static final int MOVIE_FAVOURITE_WITH_ID = 109;


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

     static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
         matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);
         matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);
         matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME, TRAILERS);
         matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME + "/#", MOVIE_TRAILERS);
         matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME + "/#/#", MOVIE_TRAILER_WITH_ID);
         matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME, REVIEWS);
         matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME + "/#", MOVIE_REVIEWS);
         matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME + "/#/#", MOVIE_REVIEW_WITH_ID);
         matcher.addURI(authority, MovieContract.FavouriteEntry.TABLE_NAME, MOVIE_FAVOURITE);
         matcher.addURI(authority, MovieContract.FavouriteEntry.TABLE_NAME + "/#", MOVIE_FAVOURITE_WITH_ID);
        return matcher;
    }


    private static final SQLiteQueryBuilder sMovieWithFavouriteQueryBuilder;

    static{
        sMovieWithFavouriteQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieWithFavouriteQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavouriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavouriteEntry.TABLE_NAME +
                        "." + MovieContract.FavouriteEntry.COLUMN_MOVIE_ID);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull final Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID: {
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            case TRAILERS:
            case MOVIE_TRAILERS: {
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_TRAILER_WITH_ID: {
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            }
            case REVIEWS:
            case MOVIE_REVIEWS: {
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_REVIEW_WITH_ID: {
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            }
            case MOVIE_FAVOURITE:
             {
                return MovieContract.FavouriteEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_FAVOURITE_WITH_ID: {
                return MovieContract.FavouriteEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final Uri returnUri;
        int uriCode = sUriMatcher.match(uri);
        Log.e(LOG_TAG,"uriCode is " + Integer.toString(uriCode));
        switch (uriCode) {
            case MOVIE: {
                long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                // Insert unless it exists
                if (_id > 0) {
                  //  returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);
                    returnUri = MovieContract.MovieEntry.buildMoviesUri(values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                } else {
                   // throw new SQLException("Failed to insert row into; " + uri);
                    Log.e(LOG_TAG,"Failed to insert row into; " + uri);
                    return null;
                }
                break;
            }
            case TRAILERS: {
                long _id = db.insertWithOnConflict(MovieContract.TrailerEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                // Insert unless it exists
                if (_id > 0) {
                    //  returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(values.getAsString(MovieContract.TrailerEntry.COLUMN_MOVIE_ID),
                            values.getAsString(MovieContract.TrailerEntry.COLUMN_TRAILER_ID));
                } else {
                   // throw new SQLException("Failed to insert row into; " + uri);
                    Log.e(LOG_TAG,"Failed to insert row into; " + uri);
                    return null;
                }
                break;
            }
            case REVIEWS: {
                long _id = db.insertWithOnConflict(MovieContract.ReviewEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                // Insert unless it exists
                if (_id > 0) {
                    //  returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(values.getAsString(MovieContract.ReviewEntry.COLUMN_MOVIE_ID),
                            values.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_ID));
                } else {
                    Log.e(LOG_TAG,"Failed to insert row into; " + uri);
                    return null;
                }
                break;
            }
            case MOVIE_FAVOURITE: {
                long _id = db.insertWithOnConflict(MovieContract.FavouriteEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                // Insert unless it exists
                if (_id > 0) {
                    //  returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);
                    returnUri = MovieContract.FavouriteEntry.buildFavouriteUri(values.getAsString(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID));

                } else {
                    Log.e(LOG_TAG,"Failed to insert row into; " + uri);
                    return null;
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull final Uri uri, final ContentValues values, final String selection,
                      final String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numUpdated = 0;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (match) {
            case MOVIE: {
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] { String.valueOf(ContentUris.parseId(uri)) });
                break;
            }
            case TRAILERS: {
                numUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case MOVIE_TRAILERS: {
                String mov_id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{mov_id});
                break;
            }
            case MOVIE_TRAILER_WITH_ID: {
                String mov_id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                String trailer_id = MovieContract.TrailerEntry.getTrailerIdFromUri(uri);
                numUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? AND " + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ? ",
                        new String[]{mov_id, trailer_id});
                break;
            }

            case REVIEWS: {
                numUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case MOVIE_REVIEWS: {
                String mov_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                numUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{mov_id});
                break;
            }
            case MOVIE_REVIEW_WITH_ID: {
                String mov_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                String review_id = MovieContract.ReviewEntry.getReviewIdFromUri(uri);
                numUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME,
                        values,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND " + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ",
                        new String[]{mov_id, review_id});
                break;
            }
            case MOVIE_FAVOURITE_WITH_ID: {
                String mov_id = MovieContract.FavouriteEntry.getMovieIdFromUri(uri);

                numUpdated = db.update(MovieContract.FavouriteEntry.TABLE_NAME,
                        values,
                        MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{mov_id});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }


    @Override
    public int delete(@NonNull final Uri uri, final String selection,
                      final String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case MOVIE: {
                numDeleted =
                        db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                // reset _id
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");
                break;
            }
            case MOVIE_WITH_ID: {
                numDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] { String.valueOf(ContentUris.parseId(uri)) });
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");
                break;
            }
            case TRAILERS: {
                numDeleted =
                        db.delete(MovieContract.TrailerEntry.TABLE_NAME,
                                selection,
                                selectionArgs);

                break;
            }
            case MOVIE_TRAILERS: {
                String mov_id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                numDeleted =
                        db.delete(MovieContract.TrailerEntry.TABLE_NAME,
                                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{mov_id});

                break;
            }
            case MOVIE_TRAILER_WITH_ID: {
                String mov_id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                String trailer_id = MovieContract.TrailerEntry.getTrailerIdFromUri(uri);
                numDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? AND " +  MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ? ",
                        new String[]{mov_id,trailer_id});

                break;
            }

            case REVIEWS: {
                numDeleted =
                        db.delete(MovieContract.ReviewEntry.TABLE_NAME,
                                selection,
                                selectionArgs);

                break;
            }
            case MOVIE_REVIEWS: {
                String mov_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                numDeleted =
                        db.delete(MovieContract.ReviewEntry.TABLE_NAME,
                                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{mov_id});

                break;
            }
            case MOVIE_REVIEW_WITH_ID: {
                String mov_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                String review_id = MovieContract.ReviewEntry.getReviewIdFromUri(uri);
                numDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND " +  MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ",
                        new String[]{mov_id,review_id});

                break;
            }
            case MOVIE_FAVOURITE:
            {
                numDeleted =
                        db.delete(MovieContract.FavouriteEntry.TABLE_NAME,
                                selection,
                                selectionArgs);
                break;
            }
            case MOVIE_FAVOURITE_WITH_ID: {
                String mov_id = MovieContract.FavouriteEntry.getMovieIdFromUri(uri);
                numDeleted =
                        db.delete(MovieContract.FavouriteEntry.TABLE_NAME,
                                MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{mov_id});

                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        return numDeleted;
    }


    public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder) {
        final Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // All movies selected
            case MOVIE: {
                retCursor =   sMovieWithFavouriteQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection,
                        selectionArgs, null, null, sortOrder);
                /*retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME, projection, selection,
                                selectionArgs, null, null, sortOrder);*/
                Log.e(LOG_TAG,"retCursor count is  " + String.valueOf(retCursor.getCount()));
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            // Individual Movie based on id selected
            case MOVIE_WITH_ID: {
                retCursor = sMovieWithFavouriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                                projection,
                             MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{String.valueOf(ContentUris.parseId(uri))},
                                null,
                                null,
                                sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.TrailerEntry.TABLE_NAME, projection, selection,
                                selectionArgs, null, null, sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case MOVIE_TRAILERS: {
                String mov_id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.TrailerEntry.TABLE_NAME,
                                projection,
                                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{mov_id},
                                null,
                                null,
                                sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            // Individual Movie based on id selected
            case MOVIE_TRAILER_WITH_ID: {
                String mov_id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                String trailer_id = MovieContract.TrailerEntry.getTrailerIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME,
                                projection,
                                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? AND " +  MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ? ",
                                new String[]{mov_id,trailer_id},
                                null,
                                null,
                                sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.ReviewEntry.TABLE_NAME, projection, selection,
                                selectionArgs, null, null, sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case MOVIE_REVIEWS: {
                String mov_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.ReviewEntry.TABLE_NAME,
                                projection,
                                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{mov_id},
                                null,
                                null,
                                sortOrder);
                Log.d(LOG_TAG,"Dumping Review Cursor" + DatabaseUtils.dumpCursorToString(retCursor));
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            // Individual Movie based on id selected
            case MOVIE_REVIEW_WITH_ID: {
                String mov_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                String review_id = MovieContract.ReviewEntry.getReviewIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.ReviewEntry.TABLE_NAME,
                                projection,
                                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? AND " +  MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ",
                                new String[]{mov_id,review_id},
                                null,
                                null,
                                sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case MOVIE_FAVOURITE: {
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.FavouriteEntry.TABLE_NAME, projection, selection,
                                selectionArgs, null, null, sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case MOVIE_FAVOURITE_WITH_ID: {
                String mov_id = MovieContract.FavouriteEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(MovieContract.FavouriteEntry.TABLE_NAME,
                                projection,
                                MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{mov_id},
                                null,
                                null,
                                sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case MOVIE: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.e("ARUN789", uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                Log.e("ARUN999", uri.toString());
                return count;
            }
            case MOVIE_TRAILERS: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.e("ARUN789", uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                Log.e("ARUN999", uri.toString());
                return count;
            }
            case MOVIE_REVIEWS: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.e("ARUN789", uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                Log.e("ARUN999", uri.toString());
                return count;
            }
            case MOVIE_FAVOURITE: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.e("ARUN789", uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                Log.e("ARUN999", uri.toString());
                return count;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
