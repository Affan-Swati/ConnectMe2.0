package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class search_tab : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: search_adapter
    private val searchList = mutableListOf<search_model>()
    private val database = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_tab)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = search_adapter(searchList, currentUserId, database)
        recyclerView.adapter = adapter

        loadRecentSearches()

        val searchIcon = findViewById<ImageView>(R.id.searchIcon)
        val searchInput = findViewById<EditText>(R.id.search)

        searchIcon.setOnClickListener {
            val searchText = searchInput.text.toString().trim()
            if (searchText.isNotEmpty()) {
                saveSearchToFirebase(searchText) // Save new search
                val intent = Intent(this, search_new_users::class.java)
                intent.putExtra("searchText", searchText)
                startActivity(intent)
            }
        }

        setupBottomNav()
    }

    private fun loadRecentSearches() {
        val recentSearchesRef = database.getReference("users").child(currentUserId).child("RecentSearches")
        recentSearchesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                searchList.clear()
                for (searchSnapshot in snapshot.children) {
                    val searchText = searchSnapshot.getValue(String::class.java)
                    if (searchText != null) {
                        searchList.add(search_model(searchText))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error loading recent searches: ${error.message}")
            }
        })
    }

    private fun saveSearchToFirebase(searchText: String) {
        if (searchText.isBlank()) return // Don't save empty searches

        val recentSearchesRef = database.getReference("users").child(currentUserId).child("RecentSearches")

        recentSearchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val searches = snapshot.children.mapNotNull { it.getValue(String::class.java) }.toMutableList()

                if (!searches.contains(searchText)) {
                    searches.add(searchText)
                    recentSearchesRef.setValue(searches)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error saving search: ${error.message}")
            }
        })
    }

    private fun setupBottomNav() {
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
    }
}
