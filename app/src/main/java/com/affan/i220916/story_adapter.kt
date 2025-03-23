package com.affan.i220916

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class story_adapter(private val storyList: List<story_model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val storyItem = storyList[position]

        if (holder is OurStoryViewHolder) {
            holder.imageView.setImageResource(storyItem.image)
        } else if (holder is NormalStoryViewHolder) {
            holder.imageView.setImageResource(storyItem.image)
        }

        // Click event for the first story (User's Story)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("stories").child(userId ?: "")

            databaseRef.get().addOnSuccessListener { snapshot ->
                if (position == 0)
                {
                    if (snapshot.exists())
                    {
                        // If the user has uploaded a story, open StoryViewActivity
                        val intent = Intent(context, StoryView::class.java)
                        intent.putExtra("userId", userId)
                        context.startActivity(intent)
                    } else
                    {
                        // If the user has NOT uploaded a story, open StoryCamera
                        val intent = Intent(context, StoryCamera::class.java)
                        context.startActivity(intent)
                    }
                }
//                else {
//                    // If it's a normal story, open StoryViewActivity
//                    val intent = Intent(context, StoryViewA::class.java)
//                    intent.putExtra("storyImage", story.profileImage)
//                    context.startActivity(intent)
//                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to check story status", Toast.LENGTH_SHORT).show()
            }
            true
        }

    }
    override fun getItemCount(): Int {
        return storyList.size
    }

    class OurStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.our_story)
    }

    class NormalStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.normal_story)
    }
}