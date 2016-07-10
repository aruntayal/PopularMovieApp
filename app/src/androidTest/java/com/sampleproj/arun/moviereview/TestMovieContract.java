package com.sampleproj.arun.moviereview;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.sampleproj.arun.moviereview.Data.MovieContract;

public class TestMovieContract extends AndroidTestCase {


    private static final String TEST_MOVIE_ID = "43";
    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildMovieUri() {


        Uri MovieUri = MovieContract.MovieEntry.buildMoviesUri(43);
        assertNotNull("Error: Null Uri returned.  You must fill-in MovieUri in " +
                        "MovieContract.",
                MovieUri);
        assertEquals("Error: Movie Id not properly appended to the end of the Uri",
                TEST_MOVIE_ID, MovieUri.getLastPathSegment());
        assertEquals("Error: Movie Id Uri doesn't match our expected result",
                MovieUri.toString(),
                "content://com.sampleproj.arun.moviereview/movie/43");
    }

    public void testBuildTrailerUri() {


        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerUri("43","44");
        assertNotNull("Error: Null Uri returned.  You must fill-in MovieUri in " +
                        "MovieContract.",
                trailerUri);
        assertEquals("Error: Movie Id not properly appended to the end of the Uri",
                "44", trailerUri.getLastPathSegment());
        assertEquals("Error: Movie Id Uri doesn't match our expected result",
                trailerUri.toString(),
                "content://com.sampleproj.arun.moviereview/trailer/43/44");
    }

}