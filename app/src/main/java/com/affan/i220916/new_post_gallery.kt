package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class new_post_gallery : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_post_gallery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val crossButton = findViewById<ImageView>(R.id.cross_button)
        crossButton.setOnClickListener {
            finish()
        }

        val cameraButton = findViewById<ImageView>(R.id.camera_button)
        cameraButton.setOnClickListener {
            finish()
            intent = Intent(this, new_post_camera::class.java)
            startActivity(intent)
        }

        val nextButton = findViewById<TextView>(R.id.H2)
        nextButton.setOnClickListener {
            intent = Intent(this, new_post_share::class.java)
            startActivity(intent)
        }


        val recyclerGalleryItems = findViewById<RecyclerView>(R.id.recycler_view_gallery)
        val layoutManager = LinearLayoutManager(this)
        recyclerGalleryItems.layoutManager = layoutManager

        val galleryList = mutableListOf<post_gallery_model>()

        galleryList.add(post_gallery_model(R.drawable.affan_pfp, R.drawable.eye_pfp,R.drawable.faaira_pfp,R.drawable.adil_pfp))
        galleryList.add(post_gallery_model(R.drawable.ham_pfp , R.drawable.ham2_pfp,R.drawable.shayaan_pfp,R.drawable.affan_pfp))
        galleryList.add(post_gallery_model(R.drawable.adil_pfp, R.drawable.eye_pfp,R.drawable.faaira_pfp,R.drawable.ham_pfp))
        galleryList.add(post_gallery_model(R.drawable.shayaan_pfp, R.drawable.adil_pfp,R.drawable.affan_pfp,R.drawable.eye_pfp))
        galleryList.add(post_gallery_model(R.drawable.ham2_pfp, R.drawable.faaira_pfp,R.drawable.ham_pfp,R.drawable.shayaan_pfp))

        recyclerGalleryItems.adapter = post_gallery_adapter(galleryList)
    }
}