package com.affan.i220916

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CommentActivity : AppCompatActivity() {

    private lateinit var postId: String
    private lateinit var userId: String // Current logged-in user ID
    private lateinit var database: DatabaseReference

    private lateinit var postImageView: ImageView
    private lateinit var commentEditText: EditText
    private lateinit var commentButton: Button
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        // Get postId and userId from intent
        postId = intent.getStringExtra("postId") ?: ""
        userId = intent.getStringExtra("userId") ?: ""

        // Initialize UI elements
        postImageView = findViewById(R.id.post_image)
        commentEditText = findViewById(R.id.comment_input)
        commentButton = findViewById(R.id.add_comment_button)
        commentRecyclerView = findViewById(R.id.comment_recycler_view)

        // Setup RecyclerView
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(commentList)
        commentRecyclerView.adapter = commentAdapter

        // Firebase reference
        database = FirebaseDatabase.getInstance().reference

        // Load post details (image, etc.)
        loadPostDetails()

        // Load comments
        loadComments()

        // Add comment button click listener
        commentButton.setOnClickListener {
            addComment()
        }
    }

    private fun loadPostDetails() {
        val postRef = database.child("posts").child(postId)

        postRef.child("imageBase64").get().addOnSuccessListener { snapshot ->
            val base64Image = snapshot.value as? String
            base64Image?.let {
                val bitmap = decodeBase64ToBitmap(it)
                postImageView.setImageBitmap(bitmap)
            }
        }.addOnFailureListener {
            Log.e("CommentActivity", "Failed to load post image", it)
        }
    }

    private fun loadComments() {
        val commentsRef = database.child("posts").child(postId).child("comments")

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                for (commentSnapshot in snapshot.children) {
                    val commentData = commentSnapshot.getValue(Comment::class.java)
                    commentData?.let {
                        val userProfilePic = decodeBase64ToBitmap(it.userProfilePic)
                        val updatedComment = it.copy(userProfilePic = it.userProfilePic)
                        commentList.add(updatedComment)
                    }
                }
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommentActivity", "Failed to load comments", error.toException())
            }
        })
    }

    private fun fetchUserDetails(comment: Comment) {
        val userRef = database.child("users").child(comment.userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.child("username").value as? String ?: "Unknown"
            val profilePicBase64 = snapshot.child("profilePic").value as? String

            val fullComment = comment.copy(username = username, userProfilePic = profilePicBase64)
            commentList.add(fullComment)
            commentAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Log.e("CommentActivity", "Failed to load user details", it)
        }
    }

    private fun addComment() {
        val commentText = commentEditText.text.toString().trim()
        if (commentText.isEmpty()) return

        val commentId = database.child("posts").child(postId).child("comments").push().key ?: return
        val userRef = database.child("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.child("username").value as? String ?: "Unknown"
            val profilePicBase64 = snapshot.child("profileImageBase64").value as? String ?: ""


            val comment = Comment(
                userId = userId,
                username = username,
                userProfilePic = profilePicBase64, // Bitmap
                text = commentText,
                timestamp = getCurrentTimestamp() // Ensure timestamp is valid
            )

            // Store comment in Firebase
            database.child("posts").child(postId).child("comments").child(commentId)
                .setValue(comment)
                .addOnSuccessListener {
                    commentEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.e("CommentActivity", "Failed to add comment", e)
                    Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show()
                }

        }.addOnFailureListener { e ->
            Log.e("CommentActivity", "Failed to fetch user details", e)
            Toast.makeText(this, "Failed to fetch user details", Toast.LENGTH_SHORT).show()
        }
    }


    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("CommentActivity", "Error decoding base64 image", e)
            null
        }
    }
}
