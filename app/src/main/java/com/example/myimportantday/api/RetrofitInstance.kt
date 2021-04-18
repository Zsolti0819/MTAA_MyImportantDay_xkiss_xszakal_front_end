package com.example.myimportantday.api

import com.example.myimportantday.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: VerySimpleApi by lazy {
        retrofit.create(VerySimpleApi::class.java)
    }
}