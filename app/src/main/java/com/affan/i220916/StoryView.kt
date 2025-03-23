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
import android.net.Uri
import android.util.Base64
import android.widget.ProgressBar
import android.widget.VideoView
import java.io.File
import java.io.FileOutputStream

class StoryView : AppCompatActivity() {

    private lateinit var storyImageView: ImageView
    private lateinit var storyVideoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var deleteButton: TextView
    private lateinit var closeButton: ImageView
    private lateinit var databaseRef: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        storyImageView = findViewById(R.id.storyImageView)
        storyVideoView = findViewById(R.id.storyVideoView)
        progressBar = findViewById(R.id.progressBar)
        deleteButton = findViewById(R.id.delete_btn)
        closeButton = findViewById(R.id.close_button)

        userId = intent.getStringExtra("userId") ?: FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            fetchStory(userId!!)
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        deleteButton.setOnClickListener { deleteStory() }
        closeButton.setOnClickListener { finish() }
    }

    private fun fetchStory(userId: String) {
        databaseRef = FirebaseDatabase.getInstance().getReference("stories").child(userId)

        databaseRef.orderByKey().limitToLast(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (storySnapshot in snapshot.children) {
                    val mediaType = storySnapshot.child("mediaType").value.toString()
                    val mediaData = storySnapshot.child("mediaData").value.toString()

                    if (mediaType == "image") {
                        val base64Part = mediaData.substring(mediaData.indexOf(",") + 1)
                        val imageBytes = Base64.decode(base64Part, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        storyImageView.setImageBitmap(bitmap)
                        storyImageView.visibility = ImageView.VISIBLE
                        storyVideoView.visibility = VideoView.GONE
                        startProgressBar()
                    } else if (mediaType == "video") {
                        val base64Part = mediaData.substring(mediaData.indexOf(",") + 1)
                        val videoBytes = Base64.decode(base64Part, Base64.DEFAULT)
                        val tempFile = File.createTempFile("video", ".mp4", cacheDir)
                        FileOutputStream(tempFile).use { it.write(videoBytes) }
                        storyVideoView.setVideoURI(Uri.fromFile(tempFile))
                        storyVideoView.setOnPreparedListener { mp ->
                            mp.start()
                        }
                        storyVideoView.visibility = VideoView.VISIBLE
                        storyImageView.visibility = ImageView.GONE
                        startProgressBar()
                    } else {
                        Toast.makeText(this, "Invalid format", Toast.LENGTH_SHORT).show()
                    }
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
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete story", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startProgressBar() {
        progressBar.visibility = ProgressBar.VISIBLE
        progressBar.max = 100

        val duration = 5000L
        object : CountDownTimer(duration, 50) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((duration - millisUntilFinished) * 100 / duration).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                progressBar.progress = 100
                finish()
            }
        }.start()
    }
}