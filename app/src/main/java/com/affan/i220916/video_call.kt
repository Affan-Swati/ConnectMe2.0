package com.affan.i220916

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration

class video_call : AppCompatActivity() {
    private var rtcEngine: RtcEngine? = null
    private var appId: String? = "6943ff1db091495dbd5ab38378dfe1c3"
    private var channelName: String? = "test_channel"
    private val token = "007eJxTYPisKMV3Sn7ijy/rNtcWyczsEtrpK7Zgjn4Ei1NiYJS6wlsFBjNLE+O0NMOUJANLQxNL05SkFNPEJGMLY3OLlLRUw2Rj563P0hsCGRlqshYyMjJAIIjPw1CSWlwSn5yRmJeXmsPAAADUsSFc"
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private var uid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        val receiver = intent.getStringExtra("reciever")

        if(currentUserId < receiver.toString())
        {
            uid = 1
        }

        else
        {
            uid = 2
        }

        if (checkPermissions()) {
            initializeAgoraEngine()
        } else {
            requestPermissions()
        }

        findViewById<ImageView>(R.id.end_call).setOnClickListener {
            leaveChannel()
            finish()
        }

        var name = findViewById<TextView>(R.id.name)
        var receiverPfp: String? = null
        if (receiver != null) {
            fetchUserDetails(receiver, name) { fetchedPfp ->
                receiverPfp = fetchedPfp
            }
        }
    }

    private fun fetchUserDetails(userId: String, name: TextView, callback: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val pfpBase64 = snapshot.child("profileImageBase64").getValue(String::class.java)
                val bitmap = decodeBase64ToBitmap(pfpBase64)

                name.text = userName
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
                    runOnUiThread { setupRemoteVideo(uid) }
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    Log.d("Agora", "User offline: $uid")
                    runOnUiThread { finish() }
                }
            })

            rtcEngine?.enableVideo()
            rtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
            rtcEngine?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

            val localView = RtcEngine.CreateRendererView(this)
            localView.setZOrderMediaOverlay(true)
            findViewById<FrameLayout>(R.id.local_video_view).addView(localView)
            rtcEngine?.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0))

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

    private fun setupRemoteVideo(uid: Int) {
        val remoteView = RtcEngine.CreateRendererView(this)
        findViewById<FrameLayout>(R.id.remote_video_view).addView(remoteView)
        rtcEngine?.setupRemoteVideo(VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    override fun onDestroy() {
        super.onDestroy()
        RtcEngine.destroy()
        rtcEngine = null
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA), 1)
    }
}
