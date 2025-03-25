package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
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
            // Load Bitmaps directly
            if (profile.postImage1 != null) {
                binding.img1.setImageBitmap(profile.postImage1)
                binding.img1.setBackgroundResource(R.drawable.brown_frame)
                binding.img1.visibility = View.VISIBLE
            } else {
                binding.img1.visibility = View.GONE
            }

            if (profile.postImage2 != null) {
                binding.img2.setImageBitmap(profile.postImage2)
                binding.img2.setBackgroundResource(R.drawable.brown_frame)
                binding.img2.visibility = View.VISIBLE
            } else {
                binding.img2.visibility = View.GONE
            }

            if (profile.postImage3 != null) {
                binding.img3.setImageBitmap(profile.postImage3)
                binding.img3.setBackgroundResource(R.drawable.brown_frame)
                binding.img3.visibility = View.VISIBLE
            } else {
                binding.img3.visibility = View.GONE
            }
        }
    }
}
