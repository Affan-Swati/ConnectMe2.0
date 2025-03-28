package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class followers : AppCompatActivity() {
    private lateinit var adapter: follower_adapter
    private val searchList = mutableListOf<follower_model>()
    private val displayedUserIds = mutableSetOf<String>()
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_followers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageView>(R.id.back_button)
        backBtn.setOnClickListener {
            val intent = Intent(this, profile_tab::class.java)
            startActivity(intent)
        }

        val followingBtn = findViewById<TextView>(R.id.following)
        followingBtn.setOnClickListener {
            val intent = Intent(this, following::class.java)
            startActivity(intent)
        }

        val follower_list = mutableListOf<follower_model>()
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = follower_adapter(searchList)
        recyclerView.adapter = adapter

        searchUsersInFirebase()

        val name = findViewById<TextView>(R.id.name)
        val nameRef = database.getReference("users").child(currentUserId).child("name")

        nameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java) // Get the actual name
                name.text = userName ?: "Unknown" // Set the TextView
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching name: ${error.message}")
            }
        })


        loadUserProfile(findViewById(R.id.followers), findViewById(R.id.following))
    }

    private fun searchUsersInFirebase() {
        database.getReference("users").child(currentUserId).child("Followers")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    searchList.clear()
                    displayedUserIds.clear()

                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key ?: continue // The key is the follower's userId

                        if (!displayedUserIds.contains(userId)) {
                            displayedUserIds.add(userId)

                            // Fetch follower details
                            database.getReference("users").child(userId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        val userName = userSnapshot.child("username").getValue(String::class.java) ?: return
                                        val pfpBase64 = userSnapshot.child("profileImageBase64").getValue(String::class.java)
                                        val bitmap = decodeBase64ToBitmap(pfpBase64)

                                        searchList.add(follower_model(bitmap, userName, userId))
                                        adapter.notifyDataSetChanged()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("Firebase", "Error fetching user details: ${error.message}")
                                    }
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching followers: ${error.message}")
                }
            })
    }

    private fun loadUserProfile(followerCountText: TextView, followingCountText: TextView)
    {
        var db = FirebaseDatabase.getInstance().reference
        val currentUserId = auth.currentUser?.uid ?: return
        val userRef = db.child("users").child(currentUserId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val bio = snapshot.child("bio").getValue(String::class.java) ?: "No bio available"
                val profilePicBase64 = snapshot.child("profileImageBase64").getValue(String::class.java)

                // Check if "Followers" and "Following" nodes exist
                val followers = if (snapshot.hasChild("Followers")) snapshot.child("Followers").childrenCount.toInt() else 0
                val following = if (snapshot.hasChild("Following")) snapshot.child("Following").childrenCount.toInt() else 0

                followerCountText.text = followers.toString() + " Followers"
                followingCountText.text = following.toString() + " Following"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@followers, "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}