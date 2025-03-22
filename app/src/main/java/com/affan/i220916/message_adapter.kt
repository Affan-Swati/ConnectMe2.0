package com.affan.i220916

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class message_adapter(private val messageList: List<message_model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val OUR_MESSAGE = 1
        const val OTHER_MESSAGE = 2
        const val VANISH_OUR_MESSAGE = 3
        const val VANISH_OTHER_MESSAGE = 4
    }

    override fun getItemViewType(position: Int): Int {
        return messageList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == OUR_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.our_msg, parent, false)
            OurMsgViewHolder(view)
            } else if (viewType == VANISH_OUR_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.our_msg_dark, parent, false)
            OurMsgViewHolder(view)
            } else if (viewType == VANISH_OTHER_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.other_msg_dark, parent, false)
            OtherMsgViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.other_msg, parent, false)
            OtherMsgViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msgItem = messageList[position]

        if (holder is OurMsgViewHolder) {
            holder.textView.text = msgItem.string
        } else if (holder is OtherMsgViewHolder) {
            holder.textView.text = msgItem.string
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class OurMsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.msg)
    }

    class OtherMsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.msg)
    }
}