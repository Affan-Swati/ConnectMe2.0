package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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

class searchDMs : AppCompatActivity() {
    private lateinit var adapter: searchDMS_adapter
    private val searchList = mutableListOf<searchDMs_model>()
    private val displayedUserIds = mutableSetOf<String>()
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_dms)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
            startActivity(Intent(this, feed::class.java))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        adapter = searchDMS_adapter(searchList, this) // Properly initialize the adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val requests = findViewById<TextView>(R.id.Requests)
        requests.setOnClickListener {
            startActivity(Intent(this, follow_request::class.java))
        }

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

        searchUsersInFirebase()
    }

    private fun searchUsersInFirebase() {
        database.getReference("users").child(currentUserId).child("Following")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    searchList.clear()
                    displayedUserIds.clear()
                    val tempList = mutableListOf<searchDMs_model>() // Temporary list to reduce UI updates

                    val userFetchTasks = snapshot.children.mapNotNull { userSnapshot ->
                        val userId = userSnapshot.key ?: return@mapNotNull null
                        if (displayedUserIds.contains(userId)) return@mapNotNull null
                        displayedUserIds.add(userId)

                        database.getReference("users").child(userId)
                    }

                    if (userFetchTasks.isEmpty()) {
                        adapter.notifyDataSetChanged()
                        return
                    }

                    for (userRef in userFetchTasks) {
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val userId = userSnapshot.key ?: return
                                val userName = userSnapshot.child("username").getValue(String::class.java) ?: return
                                val pfpBase64 = userSnapshot.child("profileImageBase64").getValue(String::class.java)
                                val bitmap = decodeBase64ToBitmap(pfpBase64)

                                tempList.add(searchDMs_model(userId, userName, bitmap))

                                if (tempList.size == userFetchTasks.size) {
                                    searchList.clear()
                                    searchList.addAll(tempList)
                                    adapter.notifyDataSetChanged() // Update UI only once
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error fetching user details: ${error.message}")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching followers: ${error.message}")
                }
            })
    }
}
