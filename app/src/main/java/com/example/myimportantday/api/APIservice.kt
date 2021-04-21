package com.example.myimportantday.api

import com.example.myimportantday.models.*
import retrofit2.Call
import retrofit2.http.*

interface APIservice {

    @FormUrlEncoded
    @POST("login/")
    fun login(@Field("username") username:String, @Field("password") password: String):Call<LoginResponse>

    @ExperimentalMultiplatform
    @GET("events/all/")
    fun showAllEvents():Call<EventList>

    @ExperimentalMultiplatform
    @GET("account/")
    fun showAccountInfo():Call<AccountInfoResponse>

    @FormUrlEncoded
    @PUT("account/username/")
    fun updateUsername(@Field("username") username:String):Call<ChangeUsernameResponse>

    @FormUrlEncoded
    @PUT("account/email/")
    fun updateEmailAddress(@Field("email") email:String):Call<ChangeEmailAddressResponse>

}