package com.affan.i220916

import android.graphics.Bitmap

data class follow_req_model (
    val userID: String,
    val userName: String,
    val pfp: Bitmap?
)