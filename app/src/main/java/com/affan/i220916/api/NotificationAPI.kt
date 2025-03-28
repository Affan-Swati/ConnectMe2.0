package com.affan.i220916.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificationAPI {

    private var retrofit: Retrofit? = null

    fun create():NotificationInterface {
        if(retrofit == null)
        {
            retrofit = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(NotificationInterface::class.java)
    }

}