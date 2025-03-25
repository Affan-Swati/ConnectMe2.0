package com.affan.i220916

import java.util.Date

data class Post(
    val imageUri: String = "",
    val caption: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "", // Store user profile image
    val timestamp: Long = System.currentTimeMillis()
)
