package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

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
                            //Toast.makeText(this, "User Signed In successfully", Toast.LENGTH_SHORT).show()
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
}