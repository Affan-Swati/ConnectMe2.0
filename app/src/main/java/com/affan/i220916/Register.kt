package com.affan.i220916

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val name = findViewById<EditText>(R.id.name)
        val username = findViewById<EditText>(R.id.username)
        val phone = findViewById<EditText>(R.id.phone_no)
        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.password)

        val loginButton = findViewById<TextView>(R.id.login_button)
        loginButton.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }
        auth = FirebaseAuth.getInstance()

        val registerButton = findViewById<TextView>(R.id.register)
        registerButton.setOnClickListener {

            val name2 = name.text.toString()
            val username2 = username.text.toString()
            val phone2 = phone.text.toString()
            val email2 = email.text.toString()
            val pass2 = pass.text.toString()

            if (email2.isEmpty() || pass2.isEmpty() || name2.isEmpty() || username2.isEmpty() || phone2.isEmpty())
            {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else
            {
                auth.createUserWithEmailAndPassword(email2, pass2)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful)
                        {
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                            finish()
                            val intent = Intent(this, login::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
    }
}