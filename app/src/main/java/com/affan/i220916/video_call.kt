package com.affan.i220916

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.affan.i220916.databinding.ActivityVideoCallBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration

class video_call : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding

    private val appID = "6943ff1db091495dbd5ab38378dfe1c3"
    private val channelName = "connectme2"
    private val token = "007eJxTYJhqcahu2z12zQelp2T3TbRJfbZGv9xlsZvhUtXwEr9V6kEKDGaWJsZpaYYpSQaWhiaWpilJKaaJScYWxuYWKWmphsnGyvqsGQ2BjAwdlWZMjAwQCOJzMSTn5+WlJpfkphoxMAAApAwfbA=="
    private val uid = 0

    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null

    private val PERMISSION_ID = 12
    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    // Check permissions
    private fun checkSelfPermissions(): Boolean {
        return !(ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED)
    }

    // Initialize the Agora Engine
    private fun setUpVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appID
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableVideo()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error initializing Agora: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkSelfPermissions()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_ID)
        } else {
            setUpVideoSDKEngine()
            joinCall()
        }

        binding.endCall.setOnClickListener {
            leaveCall()
        }

        findViewById<ImageView>(R.id.end_call).setOnClickListener {
            agoraEngine?.leaveChannel()
            finish()
        }
    }

    // Automatically re-join if user comes back & permissions are now granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                setUpVideoSDKEngine()
                joinCall()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.stopPreview()
        agoraEngine?.leaveChannel()

        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun leaveCall() {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine?.leaveChannel()
            showMessage("You left the channel")

            remoteSurfaceView?.visibility = GONE
            localSurfaceView?.visibility = GONE

            isJoined = false
        }
    }

    private fun showMessage(str: String) {
        runOnUiThread {
            Toast.makeText(this, str, Toast.LENGTH_LONG).show()
        }
    }

    private fun joinCall() {
        val option = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        }

        setupLocalVideo()
        localSurfaceView?.visibility = VISIBLE
        agoraEngine?.startPreview()
        agoraEngine?.joinChannel(token, channelName, uid, option)
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined channel: $channel, uid: $uid")
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                remoteSurfaceView?.visibility = GONE
            }
        }
    }

    private fun setupLocalVideo() {
        localSurfaceView = SurfaceView(baseContext)
        localSurfaceView?.setZOrderMediaOverlay(true)
        binding.localVideoView.addView(localSurfaceView)

        agoraEngine?.setupLocalVideo(
            VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        )
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        binding.remoteVideoView.addView(remoteSurfaceView)

        agoraEngine?.setupRemoteVideo(
            VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        )
    }

}
