package com.affan.i220916

import android.graphics.Bitmap

data class Comment(
    val commentId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfilePic: String ? = "",
    val text: String = "",
    val timestamp: String = ""
)
