package com.glownia.maciej.firsttimeamplify

import android.content.Context
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Amplify

/**
 * I use a singleton design pattern to make it easily available through the application
 * and to ensure the Amplify libraries are initialized only once.
 *
 * The class initializer takes care of initializing the Amplify libraries.
 */
object Backend {

    private const val TAG = "Backend"

    fun initialize(applicationContext: Context): Backend {
        try {
            Amplify.configure(applicationContext)
            Log.i(TAG, "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }
        return this
    }
}