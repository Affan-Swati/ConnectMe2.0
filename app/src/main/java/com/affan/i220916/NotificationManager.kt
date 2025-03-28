package com.affan.i220916

import android.util.Log
import com.affan.i220916.api.NotificationAPI
import com.affan.i220916.model.Notification
import com.affan.i220916.model.NotificationData
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NotificationManager {
    fun sendNotification(userId: String, title: String, body: String) {
        FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("fcmToken")
            .get().addOnSuccessListener { snapshot ->
                val token = snapshot.getValue(String::class.java)
                if (token != null) {
                    val notification = Notification(
                        message = NotificationData(
                            token = token,
                            hashMapOf("title" to title, "body" to body)
                        )
                    )

                    NotificationAPI.create().sendNotification(notification)
                        .enqueue(object : Callback<Notification> {
                            override fun onResponse(
                                call: Call<Notification>,
                                response: Response<Notification>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("Notification", "Notification sent successfully to $userId")
                                } else {
                                    Log.e(
                                        "Notification",
                                        "Failed to send notification: ${response.errorBody()}"
                                    )
                                }
                            }

                            override fun onFailure(call: Call<Notification>, t: Throwable) {
                                Log.e("Notification", "Failed to send notification: ${t.message}")
                            }
                        })
                } else {
                    Log.e("Notification", "Failed to retrieve FCM token for user: $userId")
                }
            }
            .addOnFailureListener {
                Log.e("Notification", "Error fetching FCM token: ${it.message}")
            }
    }
}
