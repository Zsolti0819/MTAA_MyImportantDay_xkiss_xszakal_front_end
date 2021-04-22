package com.example.myimportantday.api

import com.example.myimportantday.models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

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
    @GET("events/{date}/")
    fun showAllEvents(@Path("date") date: String):Call<EventList>

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

    @FormUrlEncoded
    @POST("events/")
    fun postEvent(
        @Field("subject") subject:String,
        @Field("date") date: String,
        @Field("place") place:String,
        @Field("priority") priority:String,
        @Field("advanced") advanced:String,
        @Field("pic") pic: MultipartBody.Part
    ):Call<EventResponse>



}