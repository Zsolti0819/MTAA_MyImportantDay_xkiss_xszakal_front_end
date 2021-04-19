package com.example.myimportantday.api

import com.example.myimportantday.models.EventsResponse
import com.example.myimportantday.models.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface APIservice {

    @FormUrlEncoded
    @POST("login/")
    fun login(
        @Field("username") username:String,
        @Field("password") password: String
    ):Call<LoginResponse>

    @FormUrlEncoded
    @GET("events/all")
    fun getAllEvents(
        @Header("Authorization") token: String

    ):Call<EventsResponse>


}