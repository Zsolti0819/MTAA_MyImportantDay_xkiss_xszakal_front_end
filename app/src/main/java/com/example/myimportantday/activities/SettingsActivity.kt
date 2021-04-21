package com.example.myimportantday.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.AccountInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager

    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        apiClient = APIclient()
        sessionManager = SessionManager(this)

        apiClient.getApiService(this).showAccountInfo()
            .enqueue(object : Callback<AccountInfoResponse> {
                override fun onFailure(call: Call<AccountInfoResponse>, t: Throwable) {
                    println("FAILURE. Token ${sessionManager.fetchAuthToken()}.")
                }

                override fun onResponse(call: Call<AccountInfoResponse>, response: Response<AccountInfoResponse>) {
                    println("SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())

                    val accountInfo = response.body()

                    findViewById<TextView>(R.id.usernameField).text = response.body()?.username
                    findViewById<TextView>(R.id.emailField).text = response.body()?.email
                    println(accountInfo)
                }
            })

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