package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class follow_request : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: follow_req_adapter
    private val requestList = mutableListOf<follow_req_model>()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_follow_request)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back_button = findViewById<ImageView>(R.id.back_button)
        back_button.setOnClickListener{
            startActivity(Intent(this, searchDMs::class.java))
        }

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

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = follow_req_adapter(requestList, currentUserId)
        recyclerView.adapter = adapter

        fetchFollowRequests()
    }

    private fun fetchFollowRequests() {
        if (currentUserId.isEmpty()) return

        val requestsRef = database.getReference("users").child(currentUserId).child("Requests")
        requestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestList.clear()
                for (requestSnapshot in snapshot.children) {
                    val userID = requestSnapshot.key
                    if (!userID.isNullOrEmpty()) { // Ignore empty userIDs
                        fetchUserDetails(userID)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("follow_request", "Error fetching requests: ${error.message}")
            }
        })
    }

    private fun fetchUserDetails(userID: String) {
        val userRef = database.getReference("users").child(userID)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("username").getValue(String::class.java) ?: "Unknown"
                val pfpBase64 = snapshot.child("profileImageBase64").getValue(String::class.java) ?: ""
                val pfpBitmap = decodeBase64ToBitmap(pfpBase64)

                val followRequest = follow_req_model(userID, userName, pfpBitmap)
                requestList.add(followRequest)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("follow_request", "Error fetching user details: ${error.message}")
            }
        })
    }


    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("follow_request", "Error decoding Base64 string", e)
            null
        }
    }
}
