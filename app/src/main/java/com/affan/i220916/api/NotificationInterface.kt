package com.affan.i220916.api

import retrofit2.Call
import com.affan.i220916.AccessToken
import com.affan.i220916.model.Notification
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationInterface {

    @POST("/v1/projects/i220916/messages:send")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    fun sendNotification(
        @Body message: Notification,
        @Header("Authorization") accessToken: String = "Bearer ${AccessToken.getAccessToken()}"
    ):Call<Notification>

}