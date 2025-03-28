package com.affan.i220916

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

// Data model for user
data class User(
    val name: String = "",
    val username: String = "",
    var phone: String = "",
    var email: String = "",
    val userId: String = "",
    var profileImageBase64: String? = null,
    var bio: String = ""
) {
    fun loadProfileImage(context: Context){
        profileImageBase64 =  toBitmap(context)
    }
}

fun toBitmap(context: Context): String? {
    return try {
        // Load and optimize the image
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565  // Uses less memory than ARGB_8888
            inSampleSize = 2  // Downscale to prevent OOM
        }

        val bitmap: Bitmap? = BitmapFactory.decodeResource(context.resources, R.drawable.default_pic, options)

        if (bitmap == null) {
            Log.e("Base64Image", "Failed to decode image. Check resource ID.")
            return null
        }

        // Convert Bitmap to byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream) // Reduce quality for efficiency
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

        // Encode the byte array to Base64
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP) // NO_WRAP removes unnecessary line breaks

        // Log the Base64 string
        Log.d("Base64Image", base64String)
        base64String

    } catch (e: Exception) {
        Log.e("Base64Image", "Error encoding image: ${e.localizedMessage}")
        null
    }
}
