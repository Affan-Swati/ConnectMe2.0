package com.affan.i220916

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64

class new_post_gallery : AppCompatActivity() {
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_post_gallery)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerGalleryItems = findViewById<RecyclerView>(R.id.recycler_view_gallery)
        recyclerGalleryItems.layoutManager = LinearLayoutManager(this)

        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadGalleryImages()
        } else {
            requestPermissions(arrayOf(permission), 101)
        }

        findViewById<ImageView>(R.id.cross_button).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.camera_button).setOnClickListener {
            finish()
            startActivity(Intent(this, new_post_camera::class.java))
        }
    }

    private fun loadGalleryImages() {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        val uris = mutableListOf<Uri>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext() && uris.size < 16) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                uris.add(contentUri)
            }
        }

        val galleryList = uris.chunked(4).map { chunk ->
            post_gallery_model(
                postImage1 = chunk.getOrNull(0),
                postImage2 = chunk.getOrNull(1),
                postImage3 = chunk.getOrNull(2),
                postImage4 = chunk.getOrNull(3)
            )
        }

        val recyclerGalleryItems = findViewById<RecyclerView>(R.id.recycler_view_gallery)
        recyclerGalleryItems.adapter = post_gallery_adapter(galleryList) { clickedUri ->
            selectedImageUri = clickedUri
            findViewById<ImageView>(R.id.selected_photo).setImageURI(clickedUri)
        }

        val nextButton = findViewById<TextView>(R.id.H2)
        nextButton.setOnClickListener {
            if (selectedImageUri != null) {
                val intent = Intent(this, new_post_share::class.java)
                intent.putExtra("SELECTED_IMAGE", selectedImageUri.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}