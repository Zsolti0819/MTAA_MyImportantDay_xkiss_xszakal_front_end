package com.example.myimportantday.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.LoginScreen
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.AccountInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsScreen : AppCompatActivity() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager

    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_screen)

        val actionBar = supportActionBar
        actionBar!!.title = "Settings"
        actionBar.setDisplayHomeAsUpEnabled(true)

        apiClient = APIclient()
        sessionManager = SessionManager(this)

        apiClient.getApiService(this).showAccountInfo().enqueue(object : Callback<AccountInfoResponse> {
            override fun onFailure(call: Call<AccountInfoResponse>, t: Throwable) {
                println("[SettingsScreen] FAILURE. Is the server running?" + t.stackTrace)
            }

            override fun onResponse(call: Call<AccountInfoResponse>, response: Response<AccountInfoResponse>) {
                if (response.code() == 200) {
                    println("[SettingsScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                    findViewById<TextView>(R.id.usernameET).text = response.body()?.username
                    findViewById<TextView>(R.id.emailET).text = response.body()?.email

                    val changeUsernameButton = findViewById<Button>(R.id.changeUsernameBTN)
                    changeUsernameButton.setOnClickListener {
                        val intent = Intent(this@SettingsScreen, UsernameChangeScreen::class.java)
                        startActivity(intent)
                    }

                    val changeEmailButton = findViewById<Button>(R.id.changeEmailBTN)
                    changeEmailButton.setOnClickListener {
                        val intent = Intent(this@SettingsScreen, EmailChangeScreen::class.java)
                        startActivity(intent)
                    }

                    val changePasswordButton = findViewById<Button>(R.id.changePasswordBTN)
                    changePasswordButton.setOnClickListener {
                        val intent = Intent(this@SettingsScreen, PasswordChangeScreen::class.java)
                        startActivity(intent)
                    }

                    val logOutButton = findViewById<Button>(R.id.logoutBTN)
                    logOutButton.setOnClickListener {
                        println("[SettingsScreen] INFO. Pre-Token ${sessionManager.fetchAuthToken()}.")
                        sessionManager.deleteTokens()
                        println("[SettingsScreen] INFO. Post-Token ${sessionManager.fetchAuthToken()}.")
                        val intent = Intent(applicationContext, LoginScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                else if (response.code() == 401) {
                    println("[SettingsScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                }
            }
        })
    }
}