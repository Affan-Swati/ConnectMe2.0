package com.affan.i220916

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
    private var rtcEngine: RtcEngine? = null
    private val appId = "6943ff1db091495dbd5ab38378dfe1c3"
    private val channelName = "test_channel"
    private val token = "007eJxTYPisKMV3Sn7ijy/rNtcWyczsEtrpK7Zgjn4Ei1NiYJS6wlsFBjNLE+O0NMOUJANLQxNL05SkFNPEJGMLY3OLlLRUw2Rj563P0hsCGRlqshYyMjJAIIjPw1CSWlwSn5yRmJeXmsPAAADUsSFc"
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private val uid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_call)

        if (checkPermissions()) {
            initializeAgoraEngine()
        } else {
            requestPermissions()
        }
        val reciever = intent.getStringExtra("reciever")
        findViewById<ImageView>(R.id.end_call).setOnClickListener {
            leaveChannel()
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

    private fun initializeAgoraEngine() {
        try {
            rtcEngine = RtcEngine.create(this, appId, object : IRtcEngineEventHandler() {
                override fun onUserJoined(uid: Int, elapsed: Int) {
                    Log.d("Agora", "User joined: $uid")
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    Log.d("Agora", "User offline: $uid")
                }
            })
            joinChannel()
        } catch (e: Exception) {
            Log.e("Agora", "Error initializing Agora: ${e.message}")
        }
    }

    private fun joinChannel() {
        rtcEngine?.joinChannel(token, channelName, null, uid)
    }

    private fun leaveChannel() {
        rtcEngine?.leaveChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        RtcEngine.destroy()
        rtcEngine = null
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
    }

}
