package com.glownia.maciej.firsttimeamplify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.datastore.generated.model.NoteData
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent

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
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)
            Log.i(TAG, "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }

        /**
         * What is happening below:
         * It checks previous authentication status at application startup time.
         * When the application starts, it checks if a Cognito session already exists and updates the UserData accordingly.
         */
        Log.i(TAG, "registering hub event")

// listen to auth event
        Amplify.Hub.subscribe(HubChannel.AUTH) { hubEvent: HubEvent<*> ->

            when (hubEvent.name) {
                InitializationStatus.SUCCEEDED.toString() -> {
                    Log.i(TAG, "Amplify successfully initialized")
                }
                InitializationStatus.FAILED.toString() -> {
                    Log.i(TAG, "Amplify initialization failed")
                }
                else -> {
                    when (AuthChannelEventName.valueOf(hubEvent.name)) {
                        AuthChannelEventName.SIGNED_IN -> {
                            updateUserData(true)
                            Log.i(TAG, "HUB : SIGNED_IN")
                        }
                        AuthChannelEventName.SIGNED_OUT -> {
                            updateUserData(false)
                            Log.i(TAG, "HUB : SIGNED_OUT")
                        }
                        else -> Log.i(TAG, """HUB EVENT:${hubEvent.name}""")
                    }
                }
            }
        }

        Log.i(TAG, "retrieving session status")

// is user already authenticated (from a previous execution) ?
        Amplify.Auth.fetchAuthSession(
            { result ->
                Log.i(TAG, result.toString())
                val cognitoAuthSession = result as AWSCognitoAuthSession
                // update UI
                this.updateUserData(cognitoAuthSession.isSignedIn)
                when (cognitoAuthSession.identityId.type) {
                    AuthSessionResult.Type.SUCCESS -> Log.i(TAG,
                        "IdentityId: " + cognitoAuthSession.identityId.value)
                    AuthSessionResult.Type.FAILURE -> Log.i(TAG,
                        "IdentityId not present because: " + cognitoAuthSession.identityId.error.toString())
                }
            },
            { error -> Log.i(TAG, error.toString()) }
        )
        return this
    }

    /**
     * Call when an authentication event is received.
     * Keeps the UserData object in sync.
     */
    private fun updateUserData(withSignedInStatus: Boolean) {
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
            { result: AuthSignInResult -> Log.i(TAG, result.toString()) },
            { error: AuthException -> Log.e(TAG, error.toString()) }
        )
    }

    // pass the data from web redirect to Amplify libs
    fun handleWebUISignInResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "received requestCode : $requestCode and resultCode : $resultCode")
        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data)
        }
    }

    /**
     * These methods below work on the app data model (Note) to make it easy to interact from
     * the User Interface. These methods transparently convert Note to GraphQL's NoteData objects.
     */
    fun queryNotes() {
        Log.i(TAG, "Querying notes")

        Amplify.API.query(
            ModelQuery.list(NoteData::class.java),
            { response ->
                Log.i(TAG, "Queried")
                for (noteData in response.data) {
                    Log.i(TAG, noteData.name)
                    // TODO should add all the notes at once instead of one by one (each add triggers a UI refresh)
                    UserData.addNote(UserData.Note.from(noteData))
                }
            },
            { error -> Log.e(TAG, "Query failure", error) }
        )
    }

    fun createNote(note : UserData.Note) {
        Log.i(TAG, "Creating notes")

        Amplify.API.mutate(
            ModelMutation.create(note.data),
            { response ->
                Log.i(TAG, "Created")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Created Note with id: " + response.data.id)
                }
            },
            { error -> Log.e(TAG, "Create failed", error) }
        )
    }

    fun deleteNote(note : UserData.Note?) {

        if (note == null) return

        Log.i(TAG, "Deleting note $note")

        Amplify.API.mutate(
            ModelMutation.delete(note.data),
            { response ->
                Log.i(TAG, "Deleted")
                if (response.hasErrors()) {
                    Log.e(TAG, response.errors.first().message)
                } else {
                    Log.i(TAG, "Deleted Note $response")
                }
            },
            { error -> Log.e(TAG, "Delete failed", error) }
        )
    }
}