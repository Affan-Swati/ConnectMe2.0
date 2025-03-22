package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class profile_tab : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_tab)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val homeBtn = findViewById<ImageView>(R.id.home_btn)
        homeBtn.setOnClickListener {
            finish()
            val intent = Intent(this, feed::class.java)
            startActivity(intent)
        }

        val searchBtn = findViewById<ImageView>(R.id.search_btn)
        searchBtn.setOnClickListener {
            finish()
            val intent = Intent(this, search_tab::class.java)
            startActivity(intent)
        }

        val followerBtn = findViewById<LinearLayout>(R.id.followers_btn)
        followerBtn.setOnClickListener {
            val intent = Intent(this, followers::class.java)
            startActivity(intent)
        }

        val followingBtn = findViewById<LinearLayout>(R.id.following_btn)
        followingBtn.setOnClickListener {
            val intent = Intent(this, following::class.java)
            startActivity(intent)
        }

        val edit_profile_Btn = findViewById<ImageView>(R.id.edit_button)
        edit_profile_Btn.setOnClickListener {
            val intent = Intent(this, edit_profile::class.java)
            startActivity(intent)
        }

        val postBtn = findViewById<ImageView>(R.id.post_btn)
        postBtn.setOnClickListener {
            val intent = Intent(this, new_post_gallery::class.java)
            startActivity(intent)
        }

        val contactBtn = findViewById<ImageView>(R.id.contact_btn)
        contactBtn.setOnClickListener {
            val intent = Intent(this, contacts_tab::class.java)
            startActivity(intent)
        }

        val recyclerProfilePost = findViewById<RecyclerView>(R.id.recycler_view_posts)
        val layoutManager = LinearLayoutManager(this)
        recyclerProfilePost.layoutManager = layoutManager

        val profileList = mutableListOf<profile_model>()

        profileList.add(profile_model(R.drawable.affan_pfp, R.drawable.eye_pfp,R.drawable.faaira_pfp))
        profileList.add(profile_model(R.drawable.ham_pfp , R.drawable.ham2_pfp,R.drawable.shayaan_pfp))
        profileList.add(profile_model(R.drawable.adil_pfp,))


        recyclerProfilePost.adapter = profile_adapter(profileList)
    }
}