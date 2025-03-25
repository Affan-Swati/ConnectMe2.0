package com.affan.i220916

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.affan.i220916.databinding.PostGalleryItemBinding
import com.bumptech.glide.Glide

class post_gallery_adapter(private val profiles: List<post_gallery_model> ,
                           private val onImageClicked: (Uri) -> Unit) : RecyclerView.Adapter<post_gallery_adapter.PostGalleryViewHolder>() {

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
            // Load images with Glide to handle URIs efficiently
            loadImageWithClick(profile.postImage1, binding.img1)
            loadImageWithClick(profile.postImage2, binding.img2)
            loadImageWithClick(profile.postImage3, binding.img3)
            loadImageWithClick(profile.postImage4, binding.img4)
        }

        private fun loadImageWithClick(uri: Uri?, imageView: ImageView) {
            if (uri != null) {
                Glide.with(imageView.context)
                    .load(uri)
                    .into(imageView)
                imageView.setBackgroundResource(R.drawable.brown_frame)

                // Set click listener for this image
                imageView.setOnClickListener {
                    onImageClicked(uri) // Pass the clicked URI back
                }
            } else {
                imageView.apply {
                    setImageDrawable(null)
                    background = null
                    setOnClickListener(null) // Disable click for empty slots
                }
            }
        }
    }
}