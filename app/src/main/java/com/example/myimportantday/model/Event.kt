package com.example.myimportantday.model

import java.sql.Timestamp

//data class Event (
//    var id: Int,
//    var subject:String,
//    var date:Timestamp,
//    var place:String,
//    var priority:String,
//    var advanced:String,
//    var pic:String,
//    var user:Int
//)

data class Event (
    val items: ArrayList<Items>
        )

data class Items (
    var id: Int,
    var br_court_name:String,
    var kind_name:String,
    var registration_date:Timestamp,
    var corporate_body_name:String,
    var br_section:String,
    var br_insertion:String,
    var text:String,
    var street:String,
    var postal_code:String,
    var city:String
)

