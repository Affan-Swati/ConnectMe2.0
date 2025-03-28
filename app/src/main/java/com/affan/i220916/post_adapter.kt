package com.affan.i220916

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class post_adapter(private val postList: List<post_model>) : RecyclerView.Adapter<post_adapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.userName.text = post.userName
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.postId)

        // Fetch likes and update UI
        postRef.child("likes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val likes = snapshot.children.mapNotNull { it.key } // Get user IDs who liked
                val isLiked = likes.contains(userId)

                // Update UI
                holder.likeButton.setImageResource(
                    if (isLiked) R.drawable.heart_icon else R.drawable.heart_empty_icon
                )
                holder.like_count.text = likes.size.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Fetch comments count and update UI
        postRef.child("comments").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.comment_count.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Like button click event
        holder.likeButton.setOnClickListener {
            postRef.child("likes").child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Unlike the post
                    postRef.child("likes").child(userId).removeValue().addOnSuccessListener {
                        holder.likeButton.setImageResource(R.drawable.heart_empty_icon) // Update UI
                    }
                } else {
                    // Like the post
                    postRef.child("likes").child(userId).setValue(true).addOnSuccessListener {
                        holder.likeButton.setImageResource(R.drawable.heart_icon) // Update UI
                    }
                }

                // Update like count after change
                postRef.child("likes").get().addOnSuccessListener { updatedSnapshot ->
                    holder.like_count.text = updatedSnapshot.childrenCount.toString()
                }
            }
        }

        // Comment button click event (Navigates to Comment Activity)
        holder.commentButton.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", post.postId) // post id
            intent.putExtra("userId", userId) // current userid
            context.startActivity(intent)
        }

        // Load profile image if available, else set default placeholder
        if (post.userImage != null) {
            holder.userImage.setImageBitmap(post.userImage)
        } else {
            holder.userImage.setImageResource(R.drawable.default_pic) // Default profile image
        }

        // Load post image if available, else set default placeholder
        if (post.postImage != null) {
            holder.postImage.setImageBitmap(post.postImage)
        } else {
            holder.postImage.setImageResource(R.drawable.default_feed_pic) // Default post image
        }

        // Set formatted caption with bold username
        val username = post.userName
        val caption = post.caption
        holder.caption.text = "$username: $caption"
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userImage: ImageView = itemView.findViewById(R.id.user_image)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val caption: TextView = itemView.findViewById(R.id.caption)
        val likeButton: ImageView = itemView.findViewById(R.id.like_button)
        val commentButton: ImageView = itemView.findViewById(R.id.comment_button)
        val comment_count : TextView = itemView.findViewById(R.id.comment_count)
        val like_count : TextView = itemView.findViewById(R.id.like_count)
    }
}
