package com.example.myimportantday.models

import java.sql.Date

data class EventPost (var id: Int, var subject: String, var date: Date, var place: String, var priority: String, var advanced: String, var pic: String)
