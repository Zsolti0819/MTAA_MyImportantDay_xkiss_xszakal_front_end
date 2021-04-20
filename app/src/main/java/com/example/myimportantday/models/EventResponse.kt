package com.example.myimportantday.models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class EventResponse (

    @SerializedName("id")
    var id: Int,

    @SerializedName("subject")
    var subject: String,

    @SerializedName("date")
    var date: Timestamp,

    @SerializedName("place")
    var place: String,

    @SerializedName("priority")
    var priority: String,

    @SerializedName("advanced")
    var advanced: String,

    @SerializedName("pic")
    var pic: String,

    @SerializedName("user")
    var user: Int

)