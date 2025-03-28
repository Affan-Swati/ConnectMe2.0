package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class search_new_users : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: search_new_adapter
    private val searchList = mutableListOf<search_new_users_model>()
    private val displayedUserIds = mutableSetOf<String>()
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_new_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = search_new_adapter(searchList, currentUserId)
        recyclerView.adapter = adapter

        // Get search text
        val receivedText = intent.getStringExtra("searchText")
        val searchTextView = findViewById<TextView>(R.id.search)
        searchTextView.text = receivedText

        // Search for users in Firebase
        if (!receivedText.isNullOrEmpty()) {
            searchUsersInFirebase(receivedText)
        }

        // Navigation Buttons
        findViewById<ImageView>(R.id.home_btn).setOnClickListener {
            startActivity(Intent(this, feed::class.java))
            finish()
        }
        findViewById<ImageView>(R.id.profile_btn).setOnClickListener {
            startActivity(Intent(this, profile_tab::class.java))
            finish()
        }
        findViewById<ImageView>(R.id.post_btn).setOnClickListener {
            startActivity(Intent(this, new_post_gallery::class.java))
        }
        findViewById<ImageView>(R.id.contact_btn).setOnClickListener {
            startActivity(Intent(this, contacts_tab::class.java))
        }
        findViewById<ImageView>(R.id.cross).setOnClickListener {
            startActivity(Intent(this, search_tab::class.java))
        }
    }

    private fun searchUsersInFirebase(query: String) {
        val usersRef = database.getReference("users")

        usersRef.orderByChild("username").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    searchList.clear()
                    displayedUserIds.clear()

                    for (userSnapshot in snapshot.children) {
                        val userID = userSnapshot.child("userId").getValue(String::class.java) ?: continue
                        val userName = userSnapshot.child("username").getValue(String::class.java) ?: continue
                        val pfpBase64 = userSnapshot.child("profileImageBase64").getValue(String::class.java)

                        if (userID.isNotEmpty() && userID != currentUserId) {
                            displayedUserIds.add(userID)

                            checkIfFollowing(userID) { isFollowing ->
                                val bitmap = decodeBase64ToBitmap(pfpBase64)

                                if (isFollowing) {
                                    searchList.add(search_new_users_model(userID, userName, bitmap, true, false))
                                    adapter.notifyDataSetChanged()
                                } else {
                                    checkIfRequested(userID) { isRequested ->
                                        searchList.add(search_new_users_model(userID, userName, bitmap, false, isRequested))
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error searching users: ${error.message}")
                }
            })
    }

    // Function to decode Base64 string into a Bitmap
    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("Base64", "Invalid Base64 string")
            null
        }
    }


    private fun checkIfFollowing(userID: String, callback: (Boolean) -> Unit) {
        val followingRef = database.getReference("users")
            .child(currentUserId)
            .child("Following")
            .child(userID)
        followingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists()) // True if following, false otherwise
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun checkIfRequested(userID: String, callback: (Boolean) -> Unit) {
        val requestedRef = database.getReference("users")
            .child(userID)
            .child("Requests")
            .child(currentUserId)
        requestedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }
}
