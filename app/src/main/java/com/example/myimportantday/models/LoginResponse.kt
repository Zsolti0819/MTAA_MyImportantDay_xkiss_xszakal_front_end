package com.example.myimportantday.models

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    var token: String,
    var user: User
)