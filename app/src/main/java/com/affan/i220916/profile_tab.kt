package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class profile_tab : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var recyclerProfilePost: RecyclerView
    private lateinit var profileAdapter: profile_adapter
    private val profileList = mutableListOf<profile_model>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_tab)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val logoutBtn = findViewById<TextView>(R.id.logout_button)
        val profileImage = findViewById<ImageView>(R.id.pfp)
        val nameText = findViewById<TextView>(R.id.name)
        val bioText = findViewById<TextView>(R.id.bio)
        val postCountText = findViewById<TextView>(R.id.post_count)
        val followerCountText = findViewById<TextView>(R.id.follower_count)
        val followingCountText = findViewById<TextView>(R.id.following_count)

        recyclerProfilePost = findViewById(R.id.recycler_view_posts)
        recyclerProfilePost.layoutManager = GridLayoutManager(this, 3)
        profileAdapter = profile_adapter(profileList)
        recyclerProfilePost.adapter = profileAdapter

        loadUserProfile(nameText, bioText, profileImage, postCountText, followerCountText, followingCountText)

        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        setupNavigation()
    }

    private fun loadUserProfile(
        nameText: TextView, bioText: TextView, profileImage: ImageView,
        postCountText: TextView, followerCountText: TextView, followingCountText: TextView
    ) {
        val currentUserId = auth.currentUser?.uid ?: return
        val userRef = database.child("users").child(currentUserId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                val bio = snapshot.child("bio").getValue(String::class.java) ?: "No bio available"
                val profilePicBase64 = snapshot.child("profileImageBase64").getValue(String::class.java)
                val followers = snapshot.child("followers").childrenCount.toInt()
                val following = snapshot.child("following").childrenCount.toInt()

                nameText.text = name
                bioText.text = bio
                followerCountText.text = followers.toString()
                followingCountText.text = following.toString()

                if (!profilePicBase64.isNullOrEmpty()) {
                    val bitmap = decodeBase64ToBitmap(profilePicBase64)
                    profileImage.setImageBitmap(bitmap)
                } else {
                    profileImage.setImageResource(R.drawable.profile_icon)
                }

                fetchUserPosts(currentUserId, postCountText)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@profile_tab, "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserPosts(userId: String, postCountText: TextView) {
        val postsRef = database.child("posts")

        postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileList.clear()
                val tempPostList = mutableListOf<Bitmap>()

                for (postSnapshot in snapshot.children) {
                    val imageBase64 = postSnapshot.child("imageBase64").getValue(String::class.java) ?: ""
                    val bitmap = decodeBase64ToBitmap(imageBase64)
                    if (bitmap != null) {
                        tempPostList.add(bitmap)
                    }
                }

                postCountText.text = tempPostList.size.toString()

                // Group images into sets of 3 for the profile grid
                for (i in tempPostList.indices step 3) {
                    val img1 = tempPostList.getOrNull(i)
                    val img2 = tempPostList.getOrNull(i + 1)
                    val img3 = tempPostList.getOrNull(i + 2)

                    profileList.add(profile_model(img1, img2, img3))
                }

                profileAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@profile_tab, "Failed to load posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.home_btn).setOnClickListener {
            startActivity(Intent(this, feed::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.search_btn).setOnClickListener {
            startActivity(Intent(this, search_tab::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.followers_btn).setOnClickListener {
            startActivity(Intent(this, followers::class.java))
        }

        findViewById<LinearLayout>(R.id.following_btn).setOnClickListener {
            startActivity(Intent(this, following::class.java))
        }

        findViewById<ImageView>(R.id.edit_button).setOnClickListener {
            startActivity(Intent(this, edit_profile::class.java))
        }

        findViewById<ImageView>(R.id.post_btn).setOnClickListener {
            startActivity(Intent(this, new_post_gallery::class.java))
        }

        findViewById<ImageView>(R.id.contact_btn).setOnClickListener {
            startActivity(Intent(this, contacts_tab::class.java))
        }
    }
}
