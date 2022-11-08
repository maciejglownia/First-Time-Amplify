package com.glownia.maciej.firsttimeamplify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.glownia.maciej.firsttimeamplify.databinding.ActivityAddNoteBinding
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
    }

    companion object {
        private const val TAG = "AddNoteActivity"
    }
}