package com.example.myimportantday.api

import com.example.myimportantday.models.EventList
import com.example.myimportantday.models.EventResponse
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

    @ExperimentalMultiplatform
    @GET("events/all/")
    fun getAllEvents():Call<EventList>

    @ExperimentalMultiplatform
    @GET("events/26/")
    fun getSpecificEvents():Call<EventResponse>


}