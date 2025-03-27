package com.affan.i220916

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class search_adapter(
    private val items: MutableList<search_model>,
    private val currentUserId: String, // Pass the current user ID to the adapter
    private val database: FirebaseDatabase // Pass the Firebase database instance
) : RecyclerView.Adapter<search_adapter.searchViewHolder>() {

    class searchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username)
        val cross: ImageView = itemView.findViewById(R.id.cross)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return searchViewHolder(view)
    }

    override fun onBindViewHolder(holder: searchViewHolder, position: Int) {
        val item = items[position]
        holder.userName.text = item.userName

        holder.userName.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, search_new_users::class.java)
            intent.putExtra("searchText", item.userName)
            context.startActivity(intent)
        }

        holder.cross.setOnClickListener {
            removeFromFirebase(item.userName) // Remove from Firebase
            items.removeAt(position) // Remove from local list
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size) // Refresh indices
        }
    }

    override fun getItemCount(): Int = items.size

    private fun removeFromFirebase(searchText: String) {
        val recentSearchesRef = database.getReference("users").child(currentUserId).child("RecentSearches")

        recentSearchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val searches = snapshot.children.mapNotNull { it.getValue(String::class.java) }.toMutableList()
                if (searches.contains(searchText)) {
                    searches.remove(searchText)
                    recentSearchesRef.setValue(searches) // Update Firebase with the new list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error removing search: ${error.message}")
            }
        })
    }
}
