package com.affan.i220916

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Post(
    val imageBase64: String = "",
    val caption: String = "",
    val userId: String = "",
    val likes: MutableList<String> = mutableListOf(), // List of user IDs who liked
    val comments: MutableList<Comment> = mutableListOf(), // List of comments
    val timestamp: String = getCurrentTimestamp(),
    val postId: String = ""
)

data class Comment(
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: String = getCurrentTimestamp()
)

fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
