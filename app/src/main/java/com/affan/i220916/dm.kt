package com.affan.i220916

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
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

class dm : AppCompatActivity() {

    private lateinit var screenshotObserver : ContentObserver
    private var recv_id = ""
    private var curr_userName = "Unknown" ?: null
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private var lastScreenshotPath: String? = null
    private var lastScreenshotTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dm)
        val userId = intent.getStringExtra("userId")

        findViewById<TextView>(R.id.view_profile).setOnClickListener{
            val intent = Intent(this@dm, vanish_mode::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        val handler = Handler(Looper.getMainLooper())

        screenshotObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                checkForScreenshot()
            }
        }

        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            screenshotObserver
        )

        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(currentUserId).child("name").get()
            .addOnSuccessListener { snapshot ->
                curr_userName = snapshot.getValue(String::class.java)
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
            intent.putExtra("reciever", userId)
            startActivity(intent)
        }

        val call_btn2 = findViewById<ImageView>(R.id.video_call)
        call_btn2.setOnClickListener {
            finish()
            val intent = Intent(this, video_call::class.java)
            startActivity(intent)
        }

        var name = findViewById<TextView>(R.id.name)
        var pfp = findViewById<ImageView>(R.id.pfp)

        val msgList = mutableListOf<Message>()
        val rv = findViewById<RecyclerView>(R.id.recycler_view_msgs)

        var receiverPfp: String? = null
        if (userId != null) {
            fetchUserDetails(userId, name, pfp) { fetchedPfp ->
                receiverPfp = fetchedPfp
                recv_id = userId
                val adapter = message_adapter(msgList, receiverPfp)
                rv.layoutManager = LinearLayoutManager(this)
                rv.adapter = adapter
                listenForMessages(currentUserId, userId, msgList, adapter)
            }
        }

        var message = findViewById<EditText>(R.id.type_msg)

        val sendButton = findViewById<ImageView>(R.id.send_button)
        sendButton.setOnClickListener {
            if (userId != null) {
                sendMessage(currentUserId, userId, message.text.toString())
                message.text.clear()
            }
        }
    }

    private fun listenForMessages(senderId: String, receiverId: String, msgList: MutableList<Message>, adapter: message_adapter)
    {
        var chatId = ""
        if(senderId <= receiverId)
        {
            chatId = senderId + "_" + receiverId
        }
        else
        {
            chatId = receiverId + "_" + senderId
        }
        val messagesRef = FirebaseDatabase.getInstance().getReference("chats/$chatId/messages")

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
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun sendMessage(senderId: String, receiverId: String, messageText: String)
    {
        NotificationManager.sendNotification(receiverId, "New Message", "$curr_userName sent you a message!")
        var chatId = ""
        if(senderId <= receiverId)
        {
            chatId = senderId + "_" + receiverId
        }
        else
        {
            chatId = receiverId + "_" + senderId
        }

        val messageRef = FirebaseDatabase.getInstance().getReference("chats/$chatId/messages").push()
        val message = Message(senderId, messageText, System.currentTimeMillis())

        messageRef.setValue(message).addOnSuccessListener {
            val userChatRef1 = FirebaseDatabase.getInstance().getReference("users/$senderId/chats/$chatId")
            val userChatRef2 = FirebaseDatabase.getInstance().getReference("users/$receiverId/chats/$chatId")
            userChatRef1.setValue(true)
            userChatRef2.setValue(true)
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

    private fun checkForScreenshot() {
        val projection = arrayOf(
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATA
        )

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val now = System.currentTimeMillis()
                if (path.lowercase().contains("screenshot") && path != lastScreenshotPath  && now - lastScreenshotTime > 5000) {
                    lastScreenshotPath = path
                    lastScreenshotTime = now
                    runOnUiThread {
                        NotificationManager.sendNotification(recv_id , "ALERT!" , "$curr_userName took a screenshot of the chat!")
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(screenshotObserver)
    }
}