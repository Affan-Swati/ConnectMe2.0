package com.affan.i220916

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Post(
    val imageBase64: String = "",
    val caption: String = "",
    val userId: String = "",
    val timestamp: String = getCurrentTimestamp(),
    val postId: String = ""
)

fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
