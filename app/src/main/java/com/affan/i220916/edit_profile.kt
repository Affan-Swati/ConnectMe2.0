package com.affan.i220916

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream

class edit_profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var profileImage: ImageView
    private lateinit var nameEdit: EditText
    private lateinit var usernameEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var bioEdit: EditText
    private lateinit var doneBtn: TextView

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        profileImage = findViewById(R.id.pfp)
        nameEdit = findViewById(R.id.name_input)
        usernameEdit = findViewById(R.id.username_input)
        phoneEdit = findViewById(R.id.contact_input)
        bioEdit = findViewById(R.id.bio_input)
        doneBtn = findViewById(R.id.done_btn)

        loadUserProfile()

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        doneBtn.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun loadUserProfile() {
        val currentUserId = auth.currentUser?.uid ?: return

        val userRef = database.child("users").child(currentUserId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val username = snapshot.child("username").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                val bio = snapshot.child("bio").getValue(String::class.java) ?: ""
                val profilePicBase64 = snapshot.child("profileImageBase64").getValue(String::class.java)

                nameEdit.setText(name)
                usernameEdit.setText(username)
                phoneEdit.setText(phone)
                bioEdit.setText(bio)

                if (!profilePicBase64.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(profilePicBase64, Base64.DEFAULT)
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    profileImage.setImageBitmap(bitmap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@edit_profile, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveProfileChanges() {
        val currentUserId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(currentUserId)

        val updatedName = nameEdit.text.toString().trim()
        val updatedUsername = usernameEdit.text.toString().trim()
        val updatedPhone = phoneEdit.text.toString().trim()
        val updatedBio = bioEdit.text.toString().trim()

        val updates = mutableMapOf<String, Any>(
            "name" to updatedName,
            "username" to updatedUsername,
            "phone" to updatedPhone,
            "bio" to updatedBio
        )

        if (selectedImageUri != null) {
            val imageBase64 = encodeImageToBase64()
            if (imageBase64 != null) {
                updates["profileImageBase64"] = imageBase64
            }
        }

        userRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, profile_tab::class.java))
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            profileImage.setImageURI(selectedImageUri) // Show selected image immediately
        }
    }

    private fun encodeImageToBase64(): String? {
        val bitmapDrawable = profileImage.drawable as? BitmapDrawable ?: return null
        val bitmap = bitmapDrawable.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}
