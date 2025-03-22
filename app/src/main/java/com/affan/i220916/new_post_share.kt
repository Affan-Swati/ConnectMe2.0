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

class new_post_share : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_post_share)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val crossButton = findViewById<ImageView>(R.id.cross_button)
        crossButton.setOnClickListener {
            finish()
        }

        val shareBtn = findViewById<TextView>(R.id.share_btn)
        shareBtn.setOnClickListener {
            finish()
            intent = Intent(this, feed::class.java)
            startActivity(intent)
        }


        val recyclerShareItems = findViewById<RecyclerView>(R.id.recycler_view_share)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerShareItems.layoutManager = layoutManager

        val shareList = mutableListOf<post_share_model>()

        shareList.add(post_share_model(R.drawable.eye_pfp))
        shareList.add(post_share_model(R.drawable.adil_pfp))
        shareList.add(post_share_model(R.drawable.shayaan_pfp))
        shareList.add(post_share_model(R.drawable.affan_pfp))
        recyclerShareItems.adapter = post_share_adapter(shareList)
    }
}