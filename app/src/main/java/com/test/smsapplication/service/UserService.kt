package com.test.smsapplication.service

import com.test.smsapplication.models.DataClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserService {
    @get:GET("/sms")
    val data: Call<DataClass?>?

    @GET("/sms/status")
    fun updateStatus(@Query("status") status: String?): Call<DataClass?>?

    @PUT("/sms")
    fun updateSmsStatus(@Body smsIds: List<Int?>?): Call<Any?>?
}