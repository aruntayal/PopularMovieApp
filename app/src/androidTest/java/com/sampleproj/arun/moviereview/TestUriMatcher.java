package com.sampleproj.arun.moviereview;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.sampleproj.arun.moviereview.Data.MovieContentProvider;
import com.sampleproj.arun.moviereview.Data.MovieContract;

public class TestUriMatcher extends AndroidTestCase {

    private static final long MOVIE_ID = 141;
    private static final String MOV_ID = "141";
    private static final String TRAILER_ID = "143";
       private static final String REVIEW_ID = "143";

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID_DIR = MovieContract.MovieEntry.buildMoviesUri(MOVIE_ID);
    private static final Uri TEST_TRAILERS = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_TRAILERS_WITH_ID_DIR = MovieContract.TrailerEntry.buildTrailerUri(MOV_ID, TRAILER_ID);
    private static final Uri TEST_REVIEWS = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_WITH_ID_DIR = MovieContract.ReviewEntry.buildReviewUri(MOV_ID, REVIEW_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        assertEquals("Error: The Trailer  URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieContentProvider.MOVIE);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID_DIR), MovieContentProvider.MOVIE_WITH_ID);

    }

    public void testTrailerUriMatcher() {
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE  URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILERS), MovieContentProvider.TRAILERS);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILERS_WITH_ID_DIR), MovieContentProvider.MOVIE_TRAILER_WITH_ID);

    }

    public void testReviewUriMatcher() {
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        assertEquals("Error: The Review  URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEWS), MovieContentProvider.REVIEWS);
        assertEquals("Error: The REview With ID  URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_WITH_ID_DIR), MovieContentProvider.MOVIE_REVIEW_WITH_ID);

    }

}