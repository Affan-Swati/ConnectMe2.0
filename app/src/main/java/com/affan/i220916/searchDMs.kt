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

class searchDMs : AppCompatActivity() {
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
            val intent = Intent(this, feed::class.java)
            startActivity(intent)
        }

        val dm_list = mutableListOf<searchDMs_model>()

        dm_list.add(searchDMs_model(R.drawable.affan_pfp, "Affan Ahmad"))
        dm_list.add(searchDMs_model(R.drawable.ham_pfp, "Hamna Daud"))
        dm_list.add(searchDMs_model(R.drawable.adil_pfp, "Adil Nadeem"))
        dm_list.add(searchDMs_model(R.drawable.shayaan_pfp, "Shayaan"))
        dm_list.add(searchDMs_model(R.drawable.faaira_pfp, "Faaira"))
        dm_list.add(searchDMs_model(R.drawable.ham2_pfp, "Ham"))
        dm_list.add(searchDMs_model(R.drawable.ham_pfp, "Hamna Daud"))
        dm_list.add(searchDMs_model(R.drawable.adil_pfp, "Adil Nadeem"))
        dm_list.add(searchDMs_model(R.drawable.shayaan_pfp, "Shayaan"))
        dm_list.add(searchDMs_model(R.drawable.faaira_pfp, "Faaira"))
        dm_list.add(searchDMs_model(R.drawable.affan_pfp, "Affan Ahmad"))
        dm_list.add(searchDMs_model(R.drawable.ham2_pfp, "Ham"))

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchDMS_adapter(dm_list)

        recyclerView.post {
            val firstItemView = recyclerView.findViewHolderForAdapterPosition(0)?.itemView
            firstItemView?.setOnClickListener {
                val intent = Intent(this, dm::class.java)
                startActivity(intent)
            }
        }

    }
}