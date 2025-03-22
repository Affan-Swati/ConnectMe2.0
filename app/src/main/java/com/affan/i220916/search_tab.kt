package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class search_tab : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_tab)
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

        val search_list = mutableListOf<search_model>()

        search_list.add(search_model("Affan Ahmad"))
        search_list.add(search_model("Hamna Daud"))
        search_list.add(search_model("Adil Nadeem"))
        search_list.add(search_model("Shayaan"))
        search_list.add(search_model("Faaira"))
        search_list.add(search_model("Ham"))
        search_list.add(search_model("Ali"))
        search_list.add(search_model("Ahmad"))
        search_list.add(search_model("Mustafa"))
        search_list.add(search_model("Adeel"))
        search_list.add(search_model("Haroon"))
        search_list.add(search_model("Junaid"))

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = search_adapter(search_list)
    }
}