package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class follower_adapter(private val items: List<follower_model>) : RecyclerView.Adapter<follower_adapter.followerViewHolder>() {

    class followerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.pfp)
        val userName: TextView = itemView.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): followerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.follower_item, parent, false)
        return followerViewHolder(view)
    }

    override fun onBindViewHolder(holder: followerViewHolder, position: Int) {
        val item = items[position]
        holder.profileImage.setImageResource(item.profileImageResId)
        holder.userName.text = item.userName
    }

    override fun getItemCount(): Int = items.size
}