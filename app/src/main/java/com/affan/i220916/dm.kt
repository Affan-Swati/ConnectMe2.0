package com.affan.i220916

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class dm : AppCompatActivity() {

    private var clickCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable { clickCount = 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
            val intent = Intent(this, searchDMs::class.java)
            startActivity(intent)
        }

        val call_btn = findViewById<ImageView>(R.id.audio_call)
        call_btn.setOnClickListener {
            finish()
            val intent = Intent(this, audio_call::class.java)
            startActivity(intent)
        }

        val call_btn2 = findViewById<ImageView>(R.id.video_call)
        call_btn2.setOnClickListener {
            finish()
            val intent = Intent(this, video_call::class.java)
            startActivity(intent)
        }


        val user = 1
        val other = 2

        val msgList = mutableListOf<message_model>()
        msgList.add(message_model(user, "Hello G ki haal chaal"))
        msgList.add(message_model(other, "May theek thak aap sunaye"))
        msgList.add(message_model(user, "May bhi theek"))
        msgList.add(message_model(user, "Are you free today?" ))
        msgList.add(message_model(other, "No, I'm busy"))
        msgList.add(message_model(user, ":((((((((((((" ))
        msgList.add(message_model(other, "SIKE KIDDING"))
        msgList.add(message_model(other, "Im Free Around 10 PM" ))
        msgList.add(message_model(user, "HEHEHHE LESGOOO" ))


        val rv = findViewById<RecyclerView>(R.id.recycler_view_msgs)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = message_adapter(msgList)

        val sendButton = findViewById<ImageView>(R.id.send_button)
        sendButton.setOnClickListener {
            clickCount++
            handler.removeCallbacks(runnable)
            if (clickCount == 3) {
                clickCount = 0
                val intent = Intent(this, vanish_mode::class.java)
                startActivity(intent)
            } else {
                handler.postDelayed(runnable, 500) // Reset count after 500ms
            }
        }

    }
}