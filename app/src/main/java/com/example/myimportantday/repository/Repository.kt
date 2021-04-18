package com.example.myimportantday.repository

import com.example.myimportantday.api.RetrofitInstance
import com.example.myimportantday.model.Event

class Repository {
    suspend fun getEvents(): Event {
        return RetrofitInstance.api.getAllEvents()
    }
}