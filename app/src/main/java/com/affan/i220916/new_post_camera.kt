package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class new_post_camera : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_post_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val crossButton = findViewById<ImageView>(R.id.cross_button)
        crossButton.setOnClickListener {
            finish()
        }

        val galleryButton = findViewById<ImageView>(R.id.gallery_preview)
        galleryButton.setOnClickListener {
            finish()
            intent = Intent(this, new_post_gallery::class.java)
            startActivity(intent)
        }

        val nextButton = findViewById<TextView>(R.id.H1)
        nextButton.setOnClickListener {
            intent = Intent(this, new_post_share::class.java)
            startActivity(intent)
        }
    }
}