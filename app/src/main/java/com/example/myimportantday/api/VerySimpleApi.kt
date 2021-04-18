package com.example.myimportantday.api

import com.example.myimportantday.model.Event
import retrofit2.http.GET

interface VerySimpleApi {
    @GET("https://fiit-dbs-xkiss-app.azurewebsites.net/v1/ov/submissions/")
    suspend fun getAllEvents(): Event


}