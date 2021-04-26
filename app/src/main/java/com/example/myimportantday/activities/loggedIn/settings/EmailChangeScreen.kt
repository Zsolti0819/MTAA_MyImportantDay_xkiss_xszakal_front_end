package com.example.myimportantday.activities.loggedIn.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myimportantday.R
import com.example.myimportantday.activities.loggedIn.MainSettingsScreen
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.ChangeEmailAddressResponse
import kotlinx.android.synthetic.main.activity_email_change_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailChangeScreen : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_change_screen)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your E-mail address"
        actionBar.setDisplayHomeAsUpEnabled(true)

        changeEmailButton.setOnClickListener {

            val email = emailET.text.toString().trim()

            if(email.isEmpty()){
                emailET.error = "E-mail is required"
                emailET.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).updateEmailAddress(email).enqueue(object : Callback<ChangeEmailAddressResponse> {
                override fun onFailure(call: Call<ChangeEmailAddressResponse>, t: Throwable) {
                    println("[PassworChangeActivity] FAILURE. Is the server running?" + t.stackTrace)
                }

                override fun onResponse(call: Call<ChangeEmailAddressResponse>, response: Response<ChangeEmailAddressResponse>) {

                    when {
                        response.code() == 200 -> {
                            println("[EmailChangeScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            val intent = Intent(this@EmailChangeScreen, MainSettingsScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        response.code() == 400 -> {
                            println("[EmailChangeScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            Toast.makeText(applicationContext,"This e-mail address is used by someone else.", Toast.LENGTH_LONG).show()
                        }
                        response.code() == 401 -> {
                            println("[EmailChangeScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        }
                    }
                }
            })
        }
    }
}