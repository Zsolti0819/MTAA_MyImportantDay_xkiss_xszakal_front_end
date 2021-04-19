package com.example.myimportantday.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myimportantday.R

class EmailChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_addres_change)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your E-mail address"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}