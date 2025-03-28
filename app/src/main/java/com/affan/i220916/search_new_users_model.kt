package com.affan.i220916

import android.graphics.Bitmap

data class search_new_users_model(
    val userID: String,
    val userName: String,
    val pfp: Bitmap?,
    var isFollowing: Boolean = false,
    var isRequested: Boolean = false
)
