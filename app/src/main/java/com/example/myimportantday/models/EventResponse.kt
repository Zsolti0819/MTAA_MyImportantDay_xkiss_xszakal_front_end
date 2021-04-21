package com.example.myimportantday.models

import java.sql.Timestamp

data class EventResponse (var id: Int, var subject: String, var date: Timestamp, var place: String, var priority: String, var advanced: String, var pic: String, var user: Int)