package com.affan.i220916

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class post_adapter(private val postList: List<post_model>) : RecyclerView.Adapter<post_adapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.userName.text = post.userName

        // Load profile image if available, else set default placeholder
        if (post.userImage != null) {
            holder.userImage.setImageBitmap(post.userImage)
        } else {
            holder.userImage.setImageResource(R.drawable.affan_pfp) // Default profile image
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
    }
}
