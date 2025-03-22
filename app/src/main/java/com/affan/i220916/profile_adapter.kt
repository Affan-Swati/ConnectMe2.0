package com.affan.i220916

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.affan.i220916.databinding.ProfileItemBinding

class profile_adapter(private val profiles: List<profile_model>) : RecyclerView.Adapter<profile_adapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int = profiles.size

    inner class ProfileViewHolder(private val binding: ProfileItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: profile_model) {
            profile.postImage1?.let {
                binding.img1.setImageResource(it)
                binding.img1.setBackgroundResource(R.drawable.brown_frame)
            } ?: run {
                binding.img1.setImageDrawable(null)
                binding.img1.background = null
            }

            profile.postImage2?.let {
                binding.img2.setImageResource(it)
                binding.img2.setBackgroundResource(R.drawable.brown_frame)
            } ?: run {
                binding.img2.setImageDrawable(null)
                binding.img2.background = null
            }

            profile.postImage3?.let {
                binding.img3.setImageResource(it)
                binding.img3.setBackgroundResource(R.drawable.brown_frame)
            } ?: run {
                binding.img3.setImageDrawable(null)
                binding.img3.background = null
            }
        }
    }
}