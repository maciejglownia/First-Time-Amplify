package com.glownia.maciej.firsttimeamplify

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.datastore.generated.model.NoteData

/**
 * The UserData class is responsible to hold user data, namely a isSignedIn flag to track current
 * authentication status and a list of Note objects.

 * These two properties are implemented according to the LiveData publish / subscribe framework.
 * It allows the Graphical User Interface (GUI) to subscribe to changes and to react accordingly.
 * To follow best practice, keep the MutableLiveData property private and only expose the readonly LiveData property.
 * Some additional boilerplate code is required when the data to publish is a list to make sure
 * observers are notified when individual components in the list are modified.

 * We also added a Note data class, just to hold the data of individual notes. Two distinct
 * properties are used for ImageName and Image. Image will be taken care of in a subsequent module.
 * Implemented the singleton design pattern for the UserData object as it allows referral to it
 * from anywhere in the application just with UserData.
 */
// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object UserData {

    private const val TAG = "UserData"

    //
    // observable properties
    //

    /**
     * Signed in status
     * Observers that are subscribed to this property will be notified when the value changes.
     * We use this mechanism to refresh the user interface automatically.
     */
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    fun setSignedIn(newValue: Boolean) {
        // use postvalue() to make the assignation on the main (UI) thread
        _isSignedIn.postValue(newValue)
    }

    // the notes
    private val _notes = MutableLiveData<MutableList<Note>>(mutableListOf())

    // please check https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun notifyObserver() {
        this._notes.notifyObserver()
    }

    fun notes(): LiveData<MutableList<Note>> = _notes
    fun addNote(n: Note) {
        val notes = _notes.value
        if (notes != null) {
            notes.add(n)
            _notes.notifyObserver()
        } else {
            Log.e(TAG, "addNote : note collection is null !!")
        }
    }

    fun deleteNote(at: Int): Note? {
        val note = _notes.value?.removeAt(at)
        _notes.notifyObserver()
        return note
    }

    fun resetNotes() {
        this._notes.value?.clear()  //used when signing out
        _notes.notifyObserver()
    }


    // a note data class
    data class Note(
        val id: String,
        val name: String,
        val description: String,
        var imageName: String? = null,
    ) {
        override fun toString(): String = name

        // bitmap image
        var image: Bitmap? = null

        // return an API NoteData from this Note object
        val data : NoteData
            get() = NoteData.builder()
                .name(this.name)
                .description(this.description)
                .image(this.imageName)
                .id(this.id)
                .build()

        /**
         * To load images, we modify the static from method on the Note data class.
         * That way, every time a NoteData object returned by the API is converted to a Note object,
         * the image is loaded in parallel. When the image is loaded, we notify the LiveData's
         * UserData to let observers know about the change. This triggers a UI refresh.
         */
        // static function to create a Note from a NoteData API object
        companion object {
            fun from(noteData : NoteData) : Note {
                val result = Note(noteData.id, noteData.name, noteData.description, noteData.image)

                if (noteData.image != null) {
                    Backend.retrieveImage(noteData.image!!) {
                        result.image = it

                        // force a UI update
                        with(UserData) { notifyObserver() }
                    }
                }

                return result
            }
        }
    }
}
