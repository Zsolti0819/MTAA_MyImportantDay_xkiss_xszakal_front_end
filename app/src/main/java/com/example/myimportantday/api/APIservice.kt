package com.example.myimportantday.api

import com.example.myimportantday.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.sql.Timestamp

interface APIservice {

    @FormUrlEncoded
    @POST("register/")
    fun register(@Field("username") username:String, @Field("email") email:String, @Field("password") password: String):Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login/")
    fun login(@Field("username") username:String, @Field("password") password: String):Call<LoginResponse>

    @ExperimentalMultiplatform
    @POST("logout/")
    fun logout():Call<LogoutResponse>

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

    @FormUrlEncoded
    @PUT("account/password/")
    fun updatePassword(@Field("old_password") old_password:String, @Field("new_password") new_password: String):Call<ChangePasswordResponse>

    @Multipart
    @POST("events/")
    fun postEvent(
        @Part("subject") subject:String,
        @Part("date") date: String,
        @Part("place") place:String,
        @Part("priority") priority:String,
        @Part("advanced") advanced:String,
        @Part pic: MultipartBody.Part?
    ):Call<EventPost>



}