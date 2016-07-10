package com.sampleproj.arun.moviereview.Sync;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class PopularMovieAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private PopularMovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PopularMovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}