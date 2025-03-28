package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class follow_req_adapter(private val items: MutableList<follow_req_model>, private val currentUserId: String) :
    RecyclerView.Adapter<follow_req_adapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username)
        val profileImage: ImageView = itemView.findViewById(R.id.pfp)
        val accept: TextView = itemView.findViewById(R.id.accept)
        val decline: TextView = itemView.findViewById(R.id.decline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.follow_req_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        holder.userName.text = item.userName
        holder.profileImage.setImageBitmap(item.pfp)

        holder.accept.setOnClickListener {
            acceptReq(item.userID)
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }

        holder.decline.setOnClickListener {
            declineReq(item.userID)
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }

    override fun getItemCount(): Int = items.size

        private fun acceptReq(userID: String)
        {
            val dbRef = FirebaseDatabase.getInstance().getReference("users")
            dbRef.child(currentUserId).child("Requests").child(userID).removeValue()
            dbRef.child(currentUserId).child("Followers").child(userID).setValue(true)
            dbRef.child(userID).child("Following").child(currentUserId).setValue(true)

            val chatId = if (currentUserId <= userID) {
                "${currentUserId}_$userID"
            } else {
                "${userID}_$currentUserId"
            }

            dbRef.child(currentUserId).child("chats").child(chatId).setValue(true)
            dbRef.child(userID).child("chats").child(chatId).setValue(true)

            val chatRef = FirebaseDatabase.getInstance().getReference("chats/$chatId/participants")
            chatRef.child(currentUserId).setValue(true)
            chatRef.child(userID).setValue(true)
        }

    private fun declineReq(userID: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.child(currentUserId).child("Requests").child(userID).removeValue()
    }

}