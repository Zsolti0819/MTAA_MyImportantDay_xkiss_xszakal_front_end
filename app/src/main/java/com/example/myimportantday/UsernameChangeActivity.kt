package com.example.myimportantday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UsernameChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username_change)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your Username"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}