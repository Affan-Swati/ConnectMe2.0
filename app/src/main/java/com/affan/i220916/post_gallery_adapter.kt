package com.affan.i220916

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.affan.i220916.databinding.PostGalleryItemBinding

class post_gallery_adapter(private val profiles: List<post_gallery_model>) : RecyclerView.Adapter<post_gallery_adapter.PostGalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostGalleryViewHolder {
        val binding = PostGalleryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostGalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostGalleryViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount(): Int = profiles.size

    inner class PostGalleryViewHolder(private val binding: PostGalleryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: post_gallery_model) {
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

            profile.postImage4?.let {
                binding.img4.setImageResource(it)
                binding.img4.setBackgroundResource(R.drawable.brown_frame)
            } ?: run {
                binding.img4.setImageDrawable(null)
                binding.img4.background = null
            }
        }
    }
}