package com.affan.i220916

import android.graphics.Bitmap

data class post_model(
    val userName: String,
    val userImage: Bitmap?,  // Decoded profile image
    val postImage: Bitmap?,  // Decoded post image
    val caption: String,
    val postId: String,
    val userId: String
)

