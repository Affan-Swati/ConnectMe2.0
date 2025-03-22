package com.affan.i220916

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.affan.i220916.databinding.PostShareItemBinding

class post_share_adapter(private val profiles: List<post_share_model>) : RecyclerView.Adapter<post_share_adapter.PostShareViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostShareViewHolder {
        val binding =
            PostShareItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostShareViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostShareViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int = profiles.size

    inner class PostShareViewHolder(private val binding: PostShareItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: post_share_model) {
            profile.postImage1?.let {
                binding.pfp.setImageResource(it)
            } ?: run {
                binding.pfp.setImageDrawable(null)

            }
        }
    }
}