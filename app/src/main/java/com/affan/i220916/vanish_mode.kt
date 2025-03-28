package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class vanish_mode : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private lateinit var chatId: String
    private lateinit var adapter: message_adapter
    private val msgList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vanish_mode)

        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val receiverId = intent.getStringExtra("userId") ?: ""

        chatId = if (senderId <= receiverId) {
            senderId + "_" + receiverId
        } else {
            receiverId + "_" + senderId
        }

        val rv = findViewById<RecyclerView>(R.id.recycler_view_msgs_dark)
        adapter = message_adapter(msgList, null)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        listenForVanishMessages()

        val messageInput = findViewById<EditText>(R.id.type_msg)
        findViewById<ImageView>(R.id.send_button).setOnClickListener {
            val text = messageInput.text.toString()
            if (text.isNotEmpty()) {
                sendVanishMessage(text)
                messageInput.text.clear()
            }
        }

        var name = findViewById<TextView>(R.id.name)
        var pfp = findViewById<ImageView>(R.id.pfp)

        var backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
            val intent = Intent(this, dm::class.java)
            intent.putExtra("userId", receiverId)
            startActivity(intent)
        }

        var receiverPfp: String? = null
        if (receiverId != null) {
            fetchUserDetails(receiverId, name, pfp) { fetchedPfp ->
                receiverPfp = fetchedPfp
                adapter = message_adapter(msgList, receiverPfp) // 🔹 Ensure adapter updates
                rv.adapter = adapter
                listenForVanishMessages()
            }
        }
    }

    private fun fetchUserDetails(userId: String, name: TextView, pfp: ImageView, callback: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val pfpBase64 = snapshot.child("profileImageBase64").getValue(String::class.java)
                val bitmap = decodeBase64ToBitmap(pfpBase64)

                name.text = userName
                pfp.setImageBitmap(bitmap)
                callback(pfpBase64) // Return PFP to adapter
            } else {
                callback(null)
            }
        }
    }

    private fun listenForVanishMessages() {
        val messagesRef = database.getReference("vanish_mode_chats/$chatId/messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                msgList.clear()
                for (msgSnapshot in snapshot.children) {
                    val message = msgSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        msgList.add(message)
                    }
                }

                adapter.notifyDataSetChanged()

                // 🔹 Scroll to the latest message
                val rv = findViewById<RecyclerView>(R.id.recycler_view_msgs_dark)
                rv.scrollToPosition(msgList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun sendVanishMessage(text: String) {
        val messagesRef = database.getReference("vanish_mode_chats/$chatId/messages").push()
        val message = Message(currentUserId, text, System.currentTimeMillis())

        messagesRef.setValue(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Delete all vanish mode messages when user exits
        database.getReference("vanish_mode_chats/$chatId/messages").removeValue()
    }
}
