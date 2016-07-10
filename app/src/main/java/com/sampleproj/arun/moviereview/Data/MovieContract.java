package com.sampleproj.arun.moviereview.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by arunt on 4/12/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.sampleproj.arun.moviereview";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final class MovieEntry implements BaseColumns {


        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_NAME = "movie_name";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_DESC = "movie_desc";
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_avg";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildMoviesUri(final long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // for building URIs on insertion
        public static Long getMovieFromUri(String uri) {
            Uri tmpUri = Uri.parse(uri);
            String tmpStr = tmpUri.getPathSegments().get(1);
            return Long.parseLong(tmpStr, 10);
        }

        public static Uri buildMovieDetailUri( long id,String movName , String desc, String poster_path)
        {
            Uri tmpUri = Uri.parse("content://" + CONTENT_AUTHORITY).buildUpon().appendQueryParameter(COLUMN_MOVIE_ID, Long.toString(id))
                                                                          .appendQueryParameter(COLUMN_MOVIE_NAME, movName)
                                                                          .appendQueryParameter(COLUMN_MOVIE_POSTER, poster_path)
                                                                          .appendQueryParameter(COLUMN_MOVIE_DESC, desc)
                                                                          .build();
            Log.e("ARUNWWW",tmpUri.toString());
            return(tmpUri);

        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TRAILER_KEY = "trailer_key";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
       public static Uri buildTrailerUri(String movie_id, String trailer_id) {
           Uri tmpUri =  CONTENT_URI.buildUpon().appendPath(movie_id).appendPath(trailer_id).build();
           return tmpUri;
        }
        public static Uri buildTrailerUri(String movie_id) {
            Uri tmpUri =  CONTENT_URI.buildUpon().appendPath(movie_id).build();
            return tmpUri;
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getTrailerIdFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }


    }

    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_AUTHOR_NAME = "author";
        public static final String COLUMN_REVIEW = "review";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildReviewUri(String movie_id, String review_id) {
            Uri tmpUri =  CONTENT_URI.buildUpon().appendPath(movie_id).appendPath(review_id).build();
            return tmpUri;
        }
        // for building URIs on insertion
        public static Uri buildReviewUri(String movie_id) {
            Uri tmpUri =  CONTENT_URI.buildUpon().appendPath(movie_id).build();
            return tmpUri;
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getReviewIdFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }


    }

    public static final class FavouriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favourite";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_FAVOURITE_STATUS = "is_favourite";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

         // for building URIs on insertion
        public static Uri buildFavouriteUri(String movie_id) {
            Uri tmpUri =  CONTENT_URI.buildUpon().appendPath(movie_id).build();
            return tmpUri;
        }
        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

}
