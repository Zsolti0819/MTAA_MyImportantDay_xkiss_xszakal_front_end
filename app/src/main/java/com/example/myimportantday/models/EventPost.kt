package com.example.myimportantday.models

import java.io.File
import java.sql.Timestamp

data class EventPost (var subject: String, var date: String, var place: String, var priority: String, var advanced: String, var pic: File)
