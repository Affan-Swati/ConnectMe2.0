package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class feed : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewPosts: RecyclerView
    private lateinit var postAdapter: post_adapter
    private val postList = mutableListOf<post_model>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feed)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigation()

        recyclerViewPosts = findViewById(R.id.recycler_view_posts)
        recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        postAdapter = post_adapter(postList)
        recyclerViewPosts.adapter = postAdapter

        addStories()

        // Add hardcoded posts first
        addHardcodedPosts()

        // Fetch posts from Firebase and add them to the list
        fetchPostsFromFirebase()
    }

    private fun addStories() {
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
    }

    private fun addHardcodedPosts() {
        postList.add(post_model("Affan Ahmad", "https://example.com/Affan_pfp.jpg", "https://example.com/eye_pfp.png", "Ayee Masla Sara Roti Da"))
        postList.add(post_model("Adil Nadeem", "https://example.com/adil_pfp.jpg", "https://example.com/shayaan_pfp.jpg", "Takhleeq On Top"))
        postAdapter.notifyDataSetChanged()
    }

    private fun fetchPostsFromFirebase() {
        database = FirebaseDatabase.getInstance().getReference("posts")

        database.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newPosts = mutableListOf<post_model>()

                for (postSnapshot in snapshot.children) {
                    val postImage = postSnapshot.child("imageUri").getValue(String::class.java) ?: ""
                    val caption = postSnapshot.child("caption").getValue(String::class.java) ?: ""
                    val userImage = postSnapshot.child("userImage").getValue(String::class.java) ?: ""
                    val userName = postSnapshot.child("userName").getValue(String::class.java) ?: ""

                    if (postImage.isNotEmpty()) {
                        newPosts.add(post_model(userName, userImage, postImage, caption))
                    }
                }

                // Add new posts at the top
                postList.addAll(0, newPosts)
                postAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@feed, "Failed to load posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.open_dms).setOnClickListener {
            startActivity(Intent(this, searchDMs::class.java))
        }
        findViewById<ImageView>(R.id.profile_btn).setOnClickListener {
            finish()
            startActivity(Intent(this, profile_tab::class.java))
        }
        findViewById<ImageView>(R.id.search_btn).setOnClickListener {
            finish()
            startActivity(Intent(this, search_tab::class.java))
        }
        findViewById<ImageView>(R.id.post_btn).setOnClickListener {
            startActivity(Intent(this, new_post_gallery::class.java))
        }
        findViewById<ImageView>(R.id.contact_btn).setOnClickListener {
            startActivity(Intent(this, contacts_tab::class.java))
        }
    }
}
