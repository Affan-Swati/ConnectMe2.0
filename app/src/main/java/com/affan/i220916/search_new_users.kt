package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class search_new_users : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_new_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val homeBtn = findViewById<ImageView>(R.id.home_btn)
        homeBtn.setOnClickListener {
            finish()
            val intent = Intent(this, feed::class.java)
            startActivity(intent)
        }

        val profileBtn = findViewById<ImageView>(R.id.profile_btn)
        profileBtn.setOnClickListener {
            finish()
            val intent = Intent(this, profile_tab::class.java)
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

        val search_list = mutableListOf<search_new_users_model>()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = search_new_adapter(search_list)

        val cross = findViewById<ImageView>(R.id.cross)
        cross.setOnClickListener{
            val intent = Intent(this, search_tab::class.java)
            startActivity(intent)
        }

        val receivedText = intent.getStringExtra("searchText")
        val search = findViewById<TextView>(R.id.search)
        search.setText(receivedText)
    }
}