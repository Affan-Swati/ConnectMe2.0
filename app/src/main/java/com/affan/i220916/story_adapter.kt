package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

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