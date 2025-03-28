package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton = findViewById<TextView>(R.id.register_button)
        registerButton.setOnClickListener {
            finish()
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        var email = findViewById<EditText>(R.id.email)
        var pass = findViewById<EditText>(R.id.password)

        val loginButton = findViewById<TextView>(R.id.login)
        loginButton.setOnClickListener {
            var email2 = email.text.toString()
            var pass2 = pass.text.toString()

            if (email2.isEmpty() || pass2.isEmpty())
            {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.signInWithEmailAndPassword(email2,pass2)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful)
                        {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            val userStatusRef = userId?.let { it1 ->
                                FirebaseDatabase.getInstance().getReference("users").child(
                                    it1
                                ).child("isOnline")
                            }
                            if (userStatusRef != null) {
                                userStatusRef.setValue(true)
                            }
                            userId?.let { saveFcmToken(it) }
                            finish()
                            var intent = Intent(this, feed::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Toast.makeText(this, "User sign in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun saveFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "FCM Token: $token")

            // Save token to Firebase Realtime Database under the user's node
            val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            database.child("fcmToken").setValue(token)
                .addOnSuccessListener {
                    Log.d("FCM", "Token saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Failed to save token: ${e.message}")
                }
        }
    }
}