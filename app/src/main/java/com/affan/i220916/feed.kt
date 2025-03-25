package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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

        loadStories()
        fetchPostsFromFirebase()
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

    private fun loadStories() {
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

    private fun fetchPostsFromFirebase() {
        database = FirebaseDatabase.getInstance().getReference("posts")

        database.orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                val usersRef = FirebaseDatabase.getInstance().getReference("users")

                for (postSnapshot in snapshot.children) {
                    val imageBase64 = postSnapshot.child("imageBase64").getValue(String::class.java) ?: ""
                    val caption = postSnapshot.child("caption").getValue(String::class.java) ?: ""
                    val userId = postSnapshot.child("userId").getValue(String::class.java) ?: ""

                    usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val userName = userSnapshot.child("name").getValue(String::class.java) ?: "Unknown User"
                            val userImageBase64 = userSnapshot.child("profileImageBase64").getValue(String::class.java) ?: ""

                            val postImageBitmap = decodeBase64ToBitmap(imageBase64)
                            val userImageBitmap = decodeBase64ToBitmap(userImageBase64)

                            if (postImageBitmap != null) {
                                postList.add(0, post_model(userName, userImageBitmap, postImageBitmap, caption))
                                postAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@feed, "Failed to load user data", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@feed, "Failed to load posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
