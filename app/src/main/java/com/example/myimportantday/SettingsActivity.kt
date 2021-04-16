package com.example.myimportantday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val actionBar = supportActionBar
        actionBar!!.title = "Settings"
        actionBar.setDisplayHomeAsUpEnabled(true)

        val changeUsernameButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.changeUsernameButton)
        changeUsernameButton.setOnClickListener {
            val intent = Intent(this, UsernameChangeActivity::class.java)
            startActivity(intent)
        }

        val changeEmailButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.changeEmailButton)
        changeEmailButton.setOnClickListener {
            val intent = Intent(this, EmailChangeActivity::class.java)
            startActivity(intent)
        }

        val changePasswordButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.changePasswordButton)
        changePasswordButton.setOnClickListener {
            val intent = Intent(this, PasswordChangeActivity::class.java)
            startActivity(intent)
        }

        val logOutButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.logoutButton)
        logOutButton.setOnClickListener {
            val intent = Intent(this, LogoutActivity::class.java)
            startActivity(intent)
        }
    }
}