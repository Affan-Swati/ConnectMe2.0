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

class contacts_tab : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacts_tab)
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

        val searchBtn = findViewById<ImageView>(R.id.search_btn)
        searchBtn.setOnClickListener {
            finish()
            val intent = Intent(this, search_tab::class.java)
            startActivity(intent)
        }

        val profileBtn = findViewById<ImageView>(R.id.profile_btn)
        profileBtn.setOnClickListener {
            finish()
            val intent = Intent(this, profile_tab::class.java)
            startActivity(intent)
        }

        val backBtn = findViewById<ImageView>(R.id.back_button)
        backBtn.setOnClickListener {
            finish()
        }

        val postBtn = findViewById<ImageView>(R.id.post_btn)
        postBtn.setOnClickListener {
            val intent = Intent(this, new_post_gallery::class.java)
            startActivity(intent)
        }


        val follower_list = mutableListOf<follower_model>()

        follower_list.add(follower_model(R.drawable.shayaan_pfp, "Shayaan"))
        follower_list.add(follower_model(R.drawable.ham_pfp, "Hamna Daud"))
        follower_list.add(follower_model(R.drawable.adil_pfp, "Adil Nadeem"))
        follower_list.add(follower_model(R.drawable.faaira_pfp, "Faaira"))
        follower_list.add(follower_model(R.drawable.eye_pfp, "Ahmad"))

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = follower_adapter(follower_list)

        val contact_list = mutableListOf<contacts_model>()

        contact_list.add(contacts_model(R.drawable.affan_pfp, "Affan Ahmad"))
        contact_list.add(contacts_model(R.drawable.ham2_pfp, "Ham"))
        contact_list.add(contacts_model(R.drawable.eye_pfp, "Ahmad"))
        contact_list.add(contacts_model(R.drawable.default_feed_pic, "Nani Amma"))
        contact_list.add(contacts_model(R.drawable.ham_pfp, "Hamna Daud"))


        val recyclerView2 = findViewById<RecyclerView>(R.id.recycler_view2)
        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView2.adapter = contacts_adapter(contact_list)

    }
}