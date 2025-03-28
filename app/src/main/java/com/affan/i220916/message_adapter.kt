package com.affan.i220916

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
//import com.google.type.Date

class message_adapter(private val messageList: List<Message>, private val receiverPfp: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val OUR_MESSAGE = 1
        const val OTHER_MESSAGE = 2
    }

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUserId) OUR_MESSAGE else OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == OUR_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.our_msg, parent, false)
            OurMsgViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.other_msg, parent, false)
            OtherMsgViewHolder(view)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msgItem = messageList[position]
        val formattedTime = SimpleDateFormat("hh:mm a").format(java.util.Date(msgItem.timestamp))

        if (holder is OurMsgViewHolder) {
            holder.textView.text = msgItem.text
            holder.timeView.text = formattedTime
        } else if (holder is OtherMsgViewHolder) {
            holder.textView.text = msgItem.text
            holder.timeView.text = formattedTime

            if (!receiverPfp.isNullOrEmpty()) {
                holder.pfpView.setImageBitmap(decodeBase64ToBitmap(receiverPfp))
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    class OurMsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.msg)
        val timeView: TextView = itemView.findViewById(R.id.msg_time)
    }

    class OtherMsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.msg)
        val timeView: TextView = itemView.findViewById(R.id.msg_time)
        val pfpView: ImageView = itemView.findViewById(R.id.pfp)
    }

    private fun decodeBase64ToBitmap(base64String: String?): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
}

annotation class SuppressLint(val value: String)
