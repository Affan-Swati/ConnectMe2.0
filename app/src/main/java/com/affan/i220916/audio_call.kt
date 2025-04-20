package com.affan.i220916

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.agora.rtc2.*
import io.agora.rtc2.IRtcEngineEventHandler.*

class audio_call : AppCompatActivity() {

    private var agoraEngine: RtcEngine? = null

    // Agora configuration values (same as video call)
    private val appID = "6943ff1db091495dbd5ab38378dfe1c3"
    private val channelName = "connectme2"
    private val token = "007eJxTYJhqcahu2z12zQelp2T3TbRJfbZGv9xlsZvhUtXwEr9V6kEKDGaWJsZpaYYpSQaWhiaWpilJKaaJScYWxuYWKWmphsnGyvqsGQ2BjAwdlWZMjAwQCOJzMSTn5+WlJpfkphoxMAAApAwfbA=="
    private val uid = 0

    private var isJoined = false

    // For voice call, we only need RECORD_AUDIO permission
    private val PERMISSION_ID = 12
    private val REQUESTED_PERMISSIONS = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    // Check permissions (only microphone needed)
    private fun checkSelfPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
    }

    // Initialize Agora engine and enable audio only
    private fun setUpAudioSDKEngine() {
        try {
            val config = RtcEngineConfig().apply {
                mContext = baseContext
                mAppId = appID
                mEventHandler = mRtcEventHandler
            }
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableAudio() // Enable audio-only mode
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error initializing Agora: ${e.message}", Toast.LENGTH_LONG).show()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_call)

        // Request permissions if necessary; else, initialize and join call
        if (!checkSelfPermissions()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_ID)
        } else {
            setUpAudioSDKEngine()
            joinCall()
        }

        val reciever = intent.getStringExtra("reciever")
        findViewById<ImageView>(R.id.end_call).setOnClickListener {
            agoraEngine?.leaveChannel()
            finish()
            intent = Intent(this, dm::class.java)
            intent.putExtra("userId", reciever)
            startActivity(intent)
        }

        var name = findViewById<TextView>(R.id.name)
        var pfp = findViewById<ImageView>(R.id.pfp)
        var receiverPfp: String? = null
        if (reciever != null) {
            fetchUserDetails(reciever, name, pfp) { fetchedPfp ->
                receiverPfp = fetchedPfp
            }
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpAudioSDKEngine()
                joinCall()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.leaveChannel()
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun joinCall() {
        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        }
        agoraEngine?.joinChannel(token, channelName, uid, options)
    }

    private fun leaveCall() {
        if (!isJoined) {
            Toast.makeText(this, "Join a channel first", Toast.LENGTH_SHORT).show()
        } else {
            agoraEngine?.leaveChannel()
            isJoined = false
            Toast.makeText(this, "You left the channel", Toast.LENGTH_SHORT).show()
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
            runOnUiThread {
                Toast.makeText(this@audio_call, "Joined channel: $channel, uid: $uid", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                Toast.makeText(this@audio_call, "User $uid joined", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Toast.makeText(this@audio_call, "User $uid left", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
