package com.example.myimportantday.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myimportantday.R

class PasswordChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your Password"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

}