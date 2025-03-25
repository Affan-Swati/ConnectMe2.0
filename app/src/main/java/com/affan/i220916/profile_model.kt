package com.affan.i220916

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

data class profile_model(val postImage: Bitmap? = null)

fun decodeBase64ToBitmap(base64String: String?): Bitmap? {
    return try {
        if (!base64String.isNullOrEmpty()) {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}
