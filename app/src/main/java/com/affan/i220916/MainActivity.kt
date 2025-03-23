package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()


         //Add a delay before starting the login activity
        Handler(Looper.getMainLooper()).postDelayed({
            if(auth.currentUser != null)
            {
                var intent = Intent(this,feed::class.java)
                startActivity(intent)
            }
            else
            {
                var intent = Intent(this, Register::class.java)
                startActivity(intent)
            }
        }, 1200) // 2000 milliseconds = 2 seconds
    }
}