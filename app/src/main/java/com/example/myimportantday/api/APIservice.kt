package com.example.myimportantday.api

import com.example.myimportantday.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @FormUrlEncoded
    @PUT("events/{id}/update/")
    fun updateEvent(@Path("id") id: Int,
                    @Part ("subject") subject: RequestBody,
                    @Part ("date") date: RequestBody,
                    @Part ("place") place: RequestBody,
                    @Part ("priority") priority: RequestBody,
                    @Part ("advanced") advanced: RequestBody,
                    @Part pic: MultipartBody.Part?):Call<EventResponse>

    @DELETE("events/{id}/delete/")
    fun deleteEvent(@Path("id") id: Int):Call<DeleteEventResponse>

    @ExperimentalMultiplatform
    @GET("events/{id}/")
    fun showEventByID(@Path("id") id: Int):Call<EventResponse>

    @ExperimentalMultiplatform
    @GET("events/by_date/{date}/")
    fun showAllEventsByDate(@Path("date") date: String):Call<EventList>

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
        @Part ("subject") subject: RequestBody,
        @Part ("date") date: RequestBody,
        @Part ("place") place: RequestBody,
        @Part ("priority") priority: RequestBody,
        @Part ("advanced") advanced: RequestBody,
        @Part pic: MultipartBody.Part?
    ):Call<EventResponse>



}