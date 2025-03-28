package com.affan.i220916

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class searchDMS_adapter(private val items: List<searchDMs_model>, private val c: Context) :
    RecyclerView.Adapter<searchDMS_adapter.searchDMsViewHolder>() {

    class searchDMsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.pfp)
        val userName: TextView = itemView.findViewById(R.id.username)
        val onlineIndicator: ImageView = itemView.findViewById(R.id.online)
        val offlineIndicator: ImageView = itemView.findViewById(R.id.offline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchDMsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_dm_item, parent, false)
        return searchDMsViewHolder(view)
    }

    override fun onBindViewHolder(holder: searchDMsViewHolder, position: Int) {
        val item = items[position]
        holder.profileImage.setImageBitmap(item.pfp)
        holder.userName.text = item.userName

        // Fetch and update online status
        val userStatusRef =
            FirebaseDatabase.getInstance().getReference("Users").child(item.userId).child("isOnline")

        userStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isOnline = snapshot.getValue(Boolean::class.java) ?: false
                if (isOnline) {
                    holder.onlineIndicator.visibility = View.VISIBLE
                    holder.offlineIndicator.visibility = View.GONE
                } else {
                    holder.onlineIndicator.visibility = View.GONE
                    holder.offlineIndicator.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        holder.itemView.setOnClickListener {
            val intent = Intent(c, dm::class.java)
            intent.putExtra("userId", item.userId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            c.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
