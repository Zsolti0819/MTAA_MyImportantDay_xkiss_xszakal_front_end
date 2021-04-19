package com.example.myimportantday.api

import com.example.myimportantday.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {

    @FormUrlEncoded
    @POST("login/")
    fun userLogin(
        @Field("username") username:String,
        @Field("password") password: String
    ):Call<LoginResponse>


}