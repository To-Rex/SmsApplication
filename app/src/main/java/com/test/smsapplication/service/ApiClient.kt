package com.test.smsapplication.service
import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient{
    private fun getRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:77/")
            //.baseUrl(Sample().getData().trim())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    val userService: UserService get() = getRetrofit().create(UserService::class.java)
}