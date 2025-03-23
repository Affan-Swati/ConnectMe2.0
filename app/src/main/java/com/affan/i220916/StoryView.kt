package com.affan.i220916

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.CountDownTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ProgressBar

class StoryView : AppCompatActivity() {

    private lateinit var storyImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var deleteButton: TextView
    private lateinit var closeButton: ImageView
    private lateinit var databaseRef: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        storyImageView = findViewById(R.id.storyImageView)
        progressBar = findViewById(R.id.progressBar)
        deleteButton = findViewById(R.id.delete_btn) // Link to delete button
        closeButton = findViewById(R.id.close_button) // Link to close button

        userId = intent.getStringExtra("userId") ?: FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            fetchStory(userId!!)
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set up delete button click listener
        deleteButton.setOnClickListener {
            deleteStory()
        }
        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchStory(userId: String) {
        databaseRef = FirebaseDatabase.getInstance().getReference("stories").child(userId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val dataUrl = snapshot.child("image").value.toString()
                if (dataUrl.startsWith("data:image/jpeg;base64,")) {
                    val base64Part = dataUrl.substring("data:image/jpeg;base64,".length)
                    val imageBytes = Base64.decode(base64Part, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    storyImageView.setImageBitmap(bitmap)

                    // Start progress bar animation
                    startProgressBar()
                } else {
                    Toast.makeText(this, "Invalid image data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No story found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load story", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStory() {
        if (userId == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        val storyRef = FirebaseDatabase.getInstance().getReference("stories").child(userId!!)

        storyRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Story deleted successfully", Toast.LENGTH_SHORT).show()
            finish() // Close activity after deletion
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete story", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startProgressBar() {
        progressBar.visibility = ProgressBar.VISIBLE
        progressBar.max = 100  // Full progress is 100%

        val duration = 5000L // 5 seconds (adjust this if needed)

        object : CountDownTimer(duration, 50) { // Updates every 50ms
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((duration - millisUntilFinished) * 100 / duration).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                progressBar.progress = 100
                finish() // Automatically close the activity when the story is done
            }
        }.start()
    }
}
