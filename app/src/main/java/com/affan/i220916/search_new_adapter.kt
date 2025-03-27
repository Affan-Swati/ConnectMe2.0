package com.affan.i220916

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class search_new_adapter(private val items: MutableList<search_new_users_model>, private val currentUserId: String) :
    RecyclerView.Adapter<search_new_adapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username)
        val profileImage: ImageView = itemView.findViewById(R.id.pfp)
        val followButton: TextView = itemView.findViewById(R.id.follow)
        val unfollowButton: TextView = itemView.findViewById(R.id.unfollow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_new_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        holder.userName.text = item.userName
        holder.profileImage.setImageBitmap(item.pfp)

        // Set Follow/Unfollow button visibility
        if (item.isFollowing) {
            holder.followButton.visibility = View.GONE
            holder.unfollowButton.visibility = View.VISIBLE
        } else {
            holder.followButton.visibility = View.VISIBLE
            holder.unfollowButton.visibility = View.GONE
        }

        // Follow User
        holder.followButton.setOnClickListener {
            followUser(item.userID)
            holder.followButton.visibility = View.GONE
            holder.unfollowButton.visibility = View.VISIBLE
        }

        // Unfollow User
        holder.unfollowButton.setOnClickListener {
            unfollowUser(item.userID)
            holder.followButton.visibility = View.VISIBLE
            holder.unfollowButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    private fun followUser(userID: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(currentUserId).child("Following").child(userID).setValue(true)
        dbRef.child(userID).child("Followers").child(currentUserId).setValue(true)
    }

    private fun unfollowUser(userID: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(currentUserId).child("Following").child(userID).removeValue()
        dbRef.child(userID).child("Followers").child(currentUserId).removeValue()
    }
}
