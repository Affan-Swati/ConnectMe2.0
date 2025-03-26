package com.affan.i220916

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class story_adapter(
    private val storyList: MutableList<story_model>,
    private val database: DatabaseReference
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CURRENT_USER_ID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    companion object {
        const val USER_STORY = 1
        const val OTHER_STORY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return storyList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER_STORY) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.our_story, parent, false)
            OurStoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.story, parent, false)
            NormalStoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val story = storyList[position]

        // Load PFP (using Glide for better performance)
        val imageBytes = Base64.decode(story.profileImageBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        when (holder) {
            is OurStoryViewHolder -> holder.imageView.setImageBitmap(bitmap)
            is NormalStoryViewHolder -> holder.imageView.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            if (position == 0) {
                // Current user's story - check if exists
                database.child("stories").child(story.userId).get()
                    .addOnSuccessListener { snapshot ->
                        val intent = if (snapshot.exists()) {
                            Intent(context, StoryView::class.java).apply {
                                putExtra("userId", story.userId)
                            }
                        } else {
                            Intent(context, StoryCamera::class.java)
                        }
                        context.startActivity(intent)
                    }
            } else {
                // Other user's story - guaranteed to exist
                Intent(context, StoryView::class.java).apply {
                    putExtra("userId", story.userId)
                }.also { context.startActivity(it) }
            }
        }
    }

    fun updateList(newList: List<story_model>) {

        storyList.clear()
        storyList.addAll(newList.reversed())

        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = storyList.size

    class OurStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.our_story)
    }

    class NormalStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.normal_story)
    }
}