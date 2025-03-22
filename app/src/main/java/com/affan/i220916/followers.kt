package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class followers : AppCompatActivity() {
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

        follower_list.add(follower_model(R.drawable.shayaan_pfp, "Shayaan"))
        follower_list.add(follower_model(R.drawable.ham_pfp, "Hamna Daud"))
        follower_list.add(follower_model(R.drawable.adil_pfp, "Adil Nadeem"))
        follower_list.add(follower_model(R.drawable.faaira_pfp, "Faaira"))
        follower_list.add(follower_model(R.drawable.ham2_pfp, "Ham"))
        follower_list.add(follower_model(R.drawable.ham_pfp, "Hamna Daud"))
        follower_list.add(follower_model(R.drawable.adil_pfp, "Adil Nadeem"))
        follower_list.add(follower_model(R.drawable.shayaan_pfp, "Shayaan"))
        follower_list.add(follower_model(R.drawable.faaira_pfp, "Faaira"))
        follower_list.add(follower_model(R.drawable.affan_pfp, "Affan Ahmad"))
        follower_list.add(follower_model(R.drawable.ham2_pfp, "Ham"))
        follower_list.add(follower_model(R.drawable.shayaan_pfp, "Shayaan"))
        follower_list.add(follower_model(R.drawable.ham_pfp, "Hamna Daud"))
        follower_list.add(follower_model(R.drawable.adil_pfp, "Adil Nadeem"))
        follower_list.add(follower_model(R.drawable.faaira_pfp, "Faaira"))
        follower_list.add(follower_model(R.drawable.ham2_pfp, "Ham"))
        follower_list.add(follower_model(R.drawable.ham_pfp, "Hamna Daud"))
        follower_list.add(follower_model(R.drawable.adil_pfp, "Adil Nadeem"))
        follower_list.add(follower_model(R.drawable.shayaan_pfp, "Shayaan"))
        follower_list.add(follower_model(R.drawable.faaira_pfp, "Faaira"))
        follower_list.add(follower_model(R.drawable.affan_pfp, "Affan Ahmad"))
        follower_list.add(follower_model(R.drawable.ham2_pfp, "Ham"))

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = follower_adapter(follower_list)
    }
}