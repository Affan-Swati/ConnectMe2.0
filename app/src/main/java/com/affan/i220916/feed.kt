package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerViewPosts: RecyclerView
    private lateinit var recyclerViewStories: RecyclerView
    private lateinit var postAdapter: post_adapter
    private lateinit var storyAdapter: story_adapter
    private val postList = mutableListOf<post_model>()
    private val storyList: MutableList<story_model> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feed)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        FirebaseDatabase.getInstance().reference.child(".info/connected")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isConnected = snapshot.getValue(Boolean::class.java) ?: false
                    Log.d("FirebaseDebug", "🔥 Firebase Connected: $isConnected")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDebug", "❌ Firebase Connection Check Failed: ${error.message}")
                }
            })

        setupNavigation()

        // Initialize RecyclerViews
        recyclerViewPosts = findViewById(R.id.recycler_view_posts)
        recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        postAdapter = post_adapter(postList)
        recyclerViewPosts.adapter = postAdapter

        recyclerViewStories = findViewById(R.id.recycler_view_stories)
        recyclerViewStories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        storyAdapter = story_adapter(storyList, FirebaseDatabase.getInstance().reference)
        recyclerViewStories.adapter = storyAdapter

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
        val storyRef = FirebaseDatabase.getInstance().getReference("stories")
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        storyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.d("FirebaseDebug", "❌ No stories found!")
                    return
                }

                val storiesList = mutableListOf<story_model>()
                var userStory: story_model? = null  // Store current user's story separately

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue

                    if (userSnapshot.childrenCount > 0) {
                        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnap: DataSnapshot) {
                                val profileImage = userSnap.child("profileImageBase64").value?.toString() ?: ""
                                val type = if (userId == currentUserId) 0 else 1  // 0 = USER_STORY, 1 = OTHER_STORY
                                val story = story_model(type, userId, profileImage)

                                if (userId == currentUserId) {
                                    userStory = story  // Store user story separately
                                } else {
                                    storiesList.add(story)
                                }

                                // Only update RecyclerView after processing all users
                                if ((storiesList.size + if (userStory != null) 1 else 0).toLong() == snapshot.childrenCount)
                                {
                                    userStory?.let { storiesList.add(0, it) }  // Always add user story at index 0
                                    storyAdapter.updateList(storiesList)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseDebug", "❌ Error fetching user details: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "❌ Error loading stories: ${error.message}")
            }
        })
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
