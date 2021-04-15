package com.example.myimportantday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EmailChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_addres_change)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your E-mail address"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}