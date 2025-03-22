package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class search_adapter(private val items: List<search_model>) : RecyclerView.Adapter<search_adapter.searchViewHolder>() {

    class searchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return searchViewHolder(view)
    }

    override fun onBindViewHolder(holder: searchViewHolder, position: Int) {
        val item = items[position]
        holder.userName.text = item.userName
    }

    override fun getItemCount(): Int = items.size
}