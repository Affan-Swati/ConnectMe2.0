package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class contacts_adapter(private val items: List<contacts_model>) : RecyclerView.Adapter<contacts_adapter.contactsViewHolder>() {

    class contactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.pfp)
        val userName: TextView = itemView.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): contactsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return contactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: contactsViewHolder, position: Int) {
        val item = items[position]
        holder.profileImage.setImageResource(item.profileImageResId)
        holder.userName.text = item.userName
    }

    override fun getItemCount(): Int = items.size
}