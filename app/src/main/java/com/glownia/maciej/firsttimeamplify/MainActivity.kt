package com.glownia.maciej.firsttimeamplify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.glownia.maciej.firsttimeamplify.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // prepare our List view and RecyclerView (cells)
        setupRecyclerView(binding.included.itemList)

        setupAuthButton(UserData)

        /**
         * Register an observer on Userdata.isSignedIn value. The closure is called when isSignedIn value changes.
         * Right now, we just change the lock icon : open when the user is authenticated and closed when the user has no session.
         */
        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
            // update UI
            Log.i(TAG, "isSignedIn changed : $isSignedUp")

            if (isSignedUp) {
                binding.fabAuth.setImageResource(R.drawable.ic_baseline_lock_open)
                Log.d(TAG, "Showing fabADD")
                binding.fabAdd.show()
                binding.fabAdd.animate().translationY(0.0F - 1.1F * binding.fabAuth.customSize)
            } else {
                binding.fabAuth.setImageResource(R.drawable.ic_baseline_lock)
                Log.d(TAG, "Hiding fabADD")
                binding.fabAdd.hide()
                binding.fabAdd.animate().translationY(0.0F)
            }
        })
    }

    // recycler view is the list of cells
    private fun setupRecyclerView(recyclerView: RecyclerView) {

        // update individual cell when the Note data are modified
        UserData.notes().observe(this, Observer<MutableList<UserData.Note>> { notes ->
            Log.d(TAG, "Note observer received ${notes.size} notes")

            // let's create a RecyclerViewAdapter that manages the individual cells
            recyclerView.adapter = NoteRecyclerViewAdapter(notes)
        })

        // register a click listener
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        // add a touch gesture handler to manager the swipe to delete gesture
        val itemTouchHelper = ItemTouchHelper(SwipeCallback(this))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun setupAuthButton(userData: UserData) {

        // register a click listener
        binding.fabAuth.setOnClickListener { view ->

            val authButton = view as FloatingActionButton

            if (userData.isSignedIn.value!!) {
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
                Backend.signOut()
            } else {
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
                Backend.signIn(this)
            }
        }
    }

    // receive the web redirect after authentication
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Backend.handleWebUISignInResponse(requestCode, resultCode, data)
    }
}