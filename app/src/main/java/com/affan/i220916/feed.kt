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

class feed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val openDMS = findViewById<ImageView>(R.id.open_dms)
        openDMS.setOnClickListener {
            val intent = Intent(this, searchDMs::class.java)
            startActivity(intent)
        }

        val profileBtn = findViewById<ImageView>(R.id.profile_btn)
        profileBtn.setOnClickListener {
            finish()
            val intent = Intent(this, profile_tab::class.java)
            startActivity(intent)
        }

        val searchBtn = findViewById<ImageView>(R.id.search_btn)
        searchBtn.setOnClickListener {
            finish()
            val intent = Intent(this, search_tab::class.java)
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

        val recyclerViewStories = findViewById<RecyclerView>(R.id.recycler_view_stories)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewStories.layoutManager = layoutManager

        val user = 1
        val normal = 2

        val storyList = mutableListOf<story_model>()
        storyList.add(story_model(user, R.drawable.affan_pfp))
        storyList.add(story_model(normal, R.drawable.ham_pfp))
        storyList.add(story_model(normal, R.drawable.adil_pfp))
        storyList.add(story_model(normal, R.drawable.ham2_pfp))
        storyList.add(story_model(normal, R.drawable.shayaan_pfp))
        storyList.add(story_model(normal, R.drawable.faaira_pfp))

        val rv = findViewById<RecyclerView>(R.id.recycler_view_stories)
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = story_adapter(storyList)

        val recyclerViewPosts = findViewById<RecyclerView>(R.id.recycler_view_posts)
        recyclerViewPosts.layoutManager = LinearLayoutManager(this)

        val postList = mutableListOf<post_model>()
        postList.add(post_model("Affan Ahmad", R.drawable.affan_pfp, R.drawable.eye_pfp, "Ayee Masla Sara Roti Da"))
        postList.add(post_model("Adil Nadeem", R.drawable.adil_pfp, R.drawable.default_feed_pic, "Takhleeq On Top"))

        recyclerViewPosts.adapter = post_adapter(postList)

    }



}