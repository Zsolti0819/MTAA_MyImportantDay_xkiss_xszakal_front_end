package com.example.myimportantday.models

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("token")
    var token: String,

    @SerializedName("user")
    var user: User
)