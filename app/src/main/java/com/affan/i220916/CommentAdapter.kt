package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.username.text = comment.username
        holder.commentText.text = comment.text

        if (comment.userProfilePic?.isNotEmpty() == true) {
            val bitmap = decodeBase64ToBitmap(comment.userProfilePic)
            if (bitmap != null) {
                holder.profilePic.setImageBitmap(bitmap)
            } else {
                holder.profilePic.setImageResource(R.drawable.default_pic) // Fallback to default
            }
        } else {
            holder.profilePic.setImageResource(R.drawable.default_pic) // Default if no image
        }
    }


    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePic: ImageView = itemView.findViewById(R.id.comment_profile_pic)
        val username: TextView = itemView.findViewById(R.id.comment_username)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
    }

    private fun formatTimestamp(timestamp: String): String {
        return java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp.toLong()))
    }
}
