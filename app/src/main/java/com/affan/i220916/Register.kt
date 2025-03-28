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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val name = findViewById<EditText>(R.id.name)
        val username = findViewById<EditText>(R.id.username)
        val phone = findViewById<EditText>(R.id.phone_no)
        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.password)

        val loginButton = findViewById<TextView>(R.id.login_button)
        loginButton.setOnClickListener {
            startActivity(Intent(this, login::class.java))
            finish()
        }

        val registerButton = findViewById<TextView>(R.id.register)
        registerButton.setOnClickListener {
            val name2 = name.text.toString()
            val username2 = username.text.toString()
            val phone2 = phone.text.toString()
            val email2 = email.text.toString()
            val pass2 = pass.text.toString()

            if (email2.isEmpty() || pass2.isEmpty() || name2.isEmpty() || username2.isEmpty() || phone2.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email2, pass2)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                // Create a user object
                                val user = User(name2, username2, phone2, email2, userId)
                                user.loadProfileImage(this)
                                database.child("users").child(userId).setValue(user)
                                    .addOnSuccessListener {
                                        val fol = hashMapOf("userId" to "")
                                        val recentSearches = hashMapOf("userId" to "")
                                        database.child("users").child(userId).child("Followers").setValue(fol)
                                        database.child("users").child(userId).child("Following").setValue(fol)
                                        database.child("users").child(userId).child("RecentSearches").setValue(recentSearches)

                                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, login::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
