package com.affan.i220916

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.affan.i220916.api.NotificationAPI
import com.affan.i220916.model.Notification
import com.affan.i220916.model.NotificationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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


        NotificationManager.sendNotification("zsgPbyNPD7QHJTrfpWurcHpYSv02", "testing", "chal gaya naa!")
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
        recyclerViewStories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        storyAdapter = story_adapter(storyList, FirebaseDatabase.getInstance().reference)
        recyclerViewStories.adapter = storyAdapter

        deleteExpiredStories()
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
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storiesList = mutableListOf<story_model>()
        var userStory: story_model? = null  // Store current user's story separately

        // Fetch all users first to count them
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                var processedUsers = 0

                // Fetch current user details first
                usersRef.child(currentUserId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnap: DataSnapshot) {
                            val profileImage =
                                userSnap.child("profileImageBase64").value?.toString() ?: ""

                            // Always add current user at the start, even if they have no story
                            userStory = story_model(
                                type = story_adapter.USER_STORY,  // 0 = USER_STORY
                                userId = currentUserId,
                                profileImageBase64 = profileImage
                            )
                            Log.e("FirebaseDebug", " Current Added to list")
                            storiesList.add(userStory!!)
                            processedUsers++

                            // Check if all users are processed
                            storyAdapter.updateList(storiesList)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(
                                "FirebaseDebug",
                                "❌ Error fetching current user details: ${error.message}"
                            )
                        }
                    })

                // Now fetch other users' stories
                storyRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val userId = userSnapshot.key ?: continue
                                if (userId == currentUserId) continue  // Skip if it's the current user

                                isFollowing(currentUserId, userId) { isFollowed ->
                                    if (isFollowed) {

                                        usersRef.child(userId)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(userSnap: DataSnapshot) {
                                                    val profileImage =
                                                        userSnap.child("profileImageBase64").value?.toString()
                                                            ?: ""
                                                    val story = story_model(
                                                        type = story_adapter.OTHER_STORY, // 1 = OTHER_STORY
                                                        userId = userId,
                                                        profileImageBase64 = profileImage
                                                    )
                                                    Log.e("FirebaseDebug", " Other Added to list")
                                                    storiesList.add(story)
                                                    processedUsers++

                                                    // Check if all users are processed

                                                    storyAdapter.updateList(storiesList)

                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Log.e(
                                                        "FirebaseDebug",
                                                        "❌ Error fetching user details: ${error.message}"
                                                    )
                                                }
                                            })
                                    }
                                }
                            }
                        } else {
                            // No other stories exist, just update with current user's icon
                            storyAdapter.updateList(storiesList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseDebug", "❌ Error loading stories: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "❌ Error fetching users: ${error.message}")
            }
        })
    }

    private fun fetchPostsFromFirebase() {
        database = FirebaseDatabase.getInstance().getReference("posts")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()
                    val usersRef = FirebaseDatabase.getInstance().getReference("users")

                    for (postSnapshot in snapshot.children) {
                        val imageBase64 =
                            postSnapshot.child("imageBase64").getValue(String::class.java) ?: ""
                        val caption =
                            postSnapshot.child("caption").getValue(String::class.java) ?: ""
                        val userId = postSnapshot.child("userId").getValue(String::class.java) ?: ""
                        val postId = postSnapshot.child("postId").getValue(String::class.java) ?: ""


                        isFollowing(currentUserId, userId) { isFollowed ->
                            if (isFollowed || userId == currentUserId) {
                                usersRef.child(userId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(userSnapshot: DataSnapshot) {
                                            val userName =
                                                userSnapshot.child("name")
                                                    .getValue(String::class.java)
                                                    ?: "Unknown User"
                                            val userImageBase64 =
                                                userSnapshot.child("profileImageBase64")
                                                    .getValue(String::class.java) ?: ""

                                            val postImageBitmap = decodeBase64ToBitmap(imageBase64)
                                            val userImageBitmap =
                                                decodeBase64ToBitmap(userImageBase64)

                                            if (postImageBitmap != null) {
                                                postList.add(
                                                    0,
                                                    post_model(
                                                        userName,
                                                        userImageBitmap,
                                                        postImageBitmap,
                                                        caption,
                                                        postId
                                                    )
                                                )
                                                postAdapter.notifyDataSetChanged()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                this@feed,
                                                "Failed to load user data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@feed,
                        "Failed to load posts: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun deleteExpiredStories() {
        val storyRef = FirebaseDatabase.getInstance().getReference("stories")
        val currentTime = System.currentTimeMillis()

        Log.d("StoryCleanup", "⏳ Checking for expired stories at $currentTime")

        storyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {  // Iterate through users
                    for (storySnapshot in userSnapshot.children) { // Iterate through their stories
                        val timestamp = storySnapshot.child("timestamp").getValue(Long::class.java)

                        if (timestamp != null) {
                            val timeElapsed = currentTime - timestamp
                            Log.d(
                                "StoryCleanup",
                                "🕒 Story ${storySnapshot.key} posted at: $timestamp, elapsed time: $timeElapsed ms"
                            )

                            if (timeElapsed >= 24 * 60 * 60 * 1000) {
                                storySnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "StoryCleanup",
                                            "✅ Story ${storySnapshot.key} deleted successfully."
                                        )
                                    }
                                    .addOnFailureListener {
                                        Log.e(
                                            "StoryCleanup",
                                            "❌ Failed to delete ${storySnapshot.key}"
                                        )
                                    }
                            }
                        } else {
                            Log.e("StoryCleanup", "⚠️ Story ${storySnapshot.key} has no timestamp!")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "❌ Error fetching stories: ${error.message}")
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

    private fun isFollowing(currentUserId: String, userId: String, callback: (Boolean) -> Unit) {
        val followingRef = FirebaseDatabase.getInstance().getReference("users")
            .child(currentUserId)
            .child("Following")
            .child(userId)
            followingRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists()) // True if following, false otherwise
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }






}
