package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
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
import java.io.ByteArrayOutputStream

class new_post_share : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_post_share)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance()
        findViewById<ImageView>(R.id.cross_button).setOnClickListener { finish() }

        selectedImageView = findViewById(R.id.selected_image)
        val selectedImageUriString = intent.getStringExtra("SELECTED_IMAGE")

        if (!selectedImageUriString.isNullOrEmpty()) {
            selectedImageUri = Uri.parse(selectedImageUriString)

            Glide.with(this)
                .load(selectedImageUri)
                .placeholder(R.drawable.default_feed_pic)
                .into(selectedImageView)
        } else {
            Toast.makeText(this, "No image received!", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.share_btn).setOnClickListener {
            val caption = findViewById<EditText>(R.id.caption).text.toString()
            if (selectedImageUri != null && caption.isNotEmpty()) {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                val postRef = database.getReference("posts").push()

                // Create a Post object instead of using a map
                val post = Post(
                    postId = postRef.key ?: "",
                    imageBase64 = encodeImageToBase64() ?: "",
                    caption = caption,
                    userId = currentUserId,
                    likes = mutableListOf(),
                    comments = mutableListOf()
                )

                postRef.setValue(post).addOnSuccessListener {
                    Toast.makeText(this, "Post shared successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, feed::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to share post", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select an image and write a caption", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeImageToBase64(): String? {
        val bitmapDrawable = selectedImageView.drawable as? BitmapDrawable ?: return null
        val bitmap = bitmapDrawable.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }
}