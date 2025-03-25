package com.affan.i220916

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class new_post_share : AppCompatActivity() {
    private var selectedImageUri: String? = null
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_post_share)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().reference

        findViewById<ImageView>(R.id.cross_button).setOnClickListener { finish() }

        selectedImageUri = intent.getStringExtra("SELECTED_IMAGE_URI")

        val selectedImageView = findViewById<ImageView>(R.id.selected_image)

        if (!selectedImageUri.isNullOrEmpty()) {
            val uri = Uri.parse(selectedImageUri)
            selectedImageView.setImageURI(uri)

            Glide.with(this)
                .load(uri)
                .into(selectedImageView)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.share_btn).setOnClickListener {
            val caption = findViewById<EditText>(R.id.caption).text.toString()
            if (!selectedImageUri.isNullOrEmpty() && caption.isNotEmpty()) {
                fetchUserDataAndUploadPost(caption)
            } else {
                Toast.makeText(this, "Please select an image and write a caption", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserDataAndUploadPost(caption: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = database.child("users").child(currentUserId)

        userRef.get().addOnSuccessListener { snapshot ->
            val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown User"
            val userImage = snapshot.child("profileImageUrl").getValue(String::class.java) ?: "https://example.com/default_pfp.jpg"

            savePostToRealtimeDatabase(userName, userImage, caption)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePostToRealtimeDatabase(userName: String, userImage: String, caption: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        val post = Post(
            imageUri = selectedImageUri ?: "",
            caption = caption,
            userId = currentUserId,
            userName = userName,
            userImage = userImage
        )

        val postsRef = database.child("posts")
        val postId = postsRef.push().key

        if (postId != null) {
            postsRef.child(postId).setValue(post)
                .addOnSuccessListener {
                    Toast.makeText(this, "Post shared successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, feed::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to share post: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Failed to generate post ID", Toast.LENGTH_SHORT).show()
        }
    }
}
