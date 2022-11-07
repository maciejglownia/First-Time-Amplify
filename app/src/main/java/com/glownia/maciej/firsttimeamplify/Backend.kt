package com.glownia.maciej.firsttimeamplify

import android.app.Activity
import android.content.Context
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.result.AuthSignInResult
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
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i(TAG, "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }
        return this
    }

    /**
     * Call when an authentication event is received.
     * Keeps the UserData object in sync.
     */
    private fun updateUserData(withSignedInStatus : Boolean) {
        UserData.setSignedIn(withSignedInStatus)
    }

    fun signOut() {
        Log.i(TAG, "Initiate Signout Sequence")

        Amplify.Auth.signOut(
            { Log.i(TAG, "Signed out!") },
            { error -> Log.e(TAG, error.toString()) }
        )
    }

    fun signIn(callingActivity: Activity) {
        Log.i(TAG, "Initiate Signin Sequence")

        Amplify.Auth.signInWithWebUI(
            callingActivity,
            { result: AuthSignInResult ->  Log.i(TAG, result.toString()) },
            { error: AuthException -> Log.e(TAG, error.toString()) }
        )
    }
}