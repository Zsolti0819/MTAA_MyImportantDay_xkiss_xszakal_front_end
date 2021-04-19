package com.example.myimportantday.models

import com.google.gson.annotations.SerializedName
import java.sql.Date

data class EventPost (
    @SerializedName("id")
    var id: Int,

    @SerializedName("subject")
    var subject: String,

    @SerializedName("date")
    var date: Date,

    @SerializedName("place")
    var place: String,

    @SerializedName("priority")
    var priority: String,

    @SerializedName("advanced")
    var advanced: String,

    @SerializedName("pic")
    var pic: String

)
