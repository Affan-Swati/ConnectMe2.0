package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.affan.i220916.databinding.ProfileItemBinding

class profile_adapter(private val profiles: List<profile_model>) : RecyclerView.Adapter<profile_adapter.ProfileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_item, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int = profiles.size

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(profile: profile_model) {
            val imgPost = itemView.findViewById<ImageView>(R.id.imgPost)
            imgPost.setImageBitmap(profile.postImage)
        }
    }
}