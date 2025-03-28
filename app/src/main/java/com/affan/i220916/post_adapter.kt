package com.affan.i220916

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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


        postRef.child("likes").get().addOnSuccessListener { snapshot ->
            val likes = snapshot.children.map { it.key ?: "" }

            // Update like button state
            if (likes.contains(userId)) {
                holder.likeButton.setImageResource(R.drawable.heart_icon) // Liked
            } else {
                holder.likeButton.setImageResource(R.drawable.heart_empty_icon) // Default
            }

            // Update like counter text
            holder.like_count.text = likes.size.toString()
        }

        // Get current comments count and update UI
        postRef.child("comments").get().addOnSuccessListener { snapshot ->
            val commentsCount = snapshot.childrenCount.toInt()
            holder.comment_count.text = commentsCount.toString()
        }

        // Like button click event
        holder.likeButton.setOnClickListener {
            postRef.child("likes").get().addOnSuccessListener { snapshot ->
                val likes = snapshot.children.map { it.key ?: "" }.toMutableList()

                if (likes.contains(userId)) {
                    likes.remove(userId)
                    holder.likeButton.setImageResource(R.drawable.heart_empty_icon) // Unliked
                } else {
                    likes.add(userId)
                    holder.likeButton.setImageResource(R.drawable.heart_icon) // Liked
                }

                // Update likes in Firebase and UI
                postRef.child("likes").setValue(likes.associateWith { true }).addOnSuccessListener {
                    holder.like_count.text = likes.size.toString()
                }
            }
        }

//        //Comment button click event (Navigates to Comment Activity)
//        holder.commentButton.setOnClickListener {
//            val intent = Intent(this, CommentsActivity::class.java)
//            intent.putExtra("postId", post.postId)
//            startActivity(intent)
//        }

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
