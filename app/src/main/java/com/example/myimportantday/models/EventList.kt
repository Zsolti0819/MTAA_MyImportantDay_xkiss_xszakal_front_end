package com.example.myimportantday.models

import com.google.gson.annotations.SerializedName

data class EventList (
    @SerializedName("events")
    var events: List<EventResponse>
    )