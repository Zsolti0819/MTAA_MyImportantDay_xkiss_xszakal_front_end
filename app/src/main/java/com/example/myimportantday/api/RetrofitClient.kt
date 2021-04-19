package com.example.myimportantday.api

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

//    private val AUTH = "Token "+ Base64.encodeToString("belalkhan:123456".toByteArray(), Base64.NO_WRAP)

    private const val BASE_URL = "http://192.168.1.15:8000"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
//                .addHeader("Authorization", AUTH)
                .method(original.method, original.body)

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

    val INSTANCE: API by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(API::class.java)
    }

}