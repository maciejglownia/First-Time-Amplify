package com.glownia.maciej.firsttimeamplify

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.glownia.maciej.firsttimeamplify.databinding.ActivityAddNoteBinding
import com.google.android.material.shape.CornerFamily
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancel.setOnClickListener {
            this.finish()
        }

        binding.addNote.setOnClickListener {

            // create a note object
            val note = UserData.Note(
                UUID.randomUUID().toString(),
                binding.name?.text.toString(),
                binding.description?.text.toString()
            )

            // store it in the backend
            Backend.createNote(note)

            // add it to UserData, this will trigger a UI refresh
            UserData.addNote(note)

            // close activity
            this.finish()
        }

        // Set up the listener for add Image button
        binding.captureImage.setOnClickListener {
            val i = Intent(
                Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, SELECT_PHOTO)
        }

        // create rounded corners for the image
        binding.image.shapeAppearanceModel = binding.image.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, 150.0f)
            .build()
    }

    companion object {
        private const val TAG = "AddNoteActivity"
        private const val SELECT_PHOTO = 100
    }
}