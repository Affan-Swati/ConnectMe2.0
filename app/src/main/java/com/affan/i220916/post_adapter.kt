package com.affan.i220916

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class post_adapter(private val postList: List<post_model>) : RecyclerView.Adapter<post_adapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.userName.text = post.userName

        // Load images using Glide (from URL)
        Glide.with(holder.itemView.context)
            .load(post.userImage) // Load user profile image
            .placeholder(R.drawable.affan_pfp) // Use default placeholder image
            .into(holder.userImage)

        Glide.with(holder.itemView.context)
            .load(post.postImage) // Load post image
            .placeholder(R.drawable.default_feed_pic) // Use default placeholder image
            .into(holder.postImage)

        // Bold username in caption
        val username = post.userName
        val caption = post.caption
        val spannableString = SpannableString("$username $caption")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.caption.text = spannableString
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
