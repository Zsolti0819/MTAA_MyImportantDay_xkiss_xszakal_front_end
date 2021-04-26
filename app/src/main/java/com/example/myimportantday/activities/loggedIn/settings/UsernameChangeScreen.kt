package com.example.myimportantday.activities.loggedIn.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myimportantday.R
import com.example.myimportantday.activities.loggedIn.MainSettingsScreen
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.ChangeUsernameResponse
import com.example.myimportantday.tools.PopUpWindow
import kotlinx.android.synthetic.main.activity_username_change_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsernameChangeScreen : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username_change_screen)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your Username"
        actionBar.setDisplayHomeAsUpEnabled(true)

        changeUsernameBTN.setOnClickListener {
            val username = username2ET.text.toString().trim()

            if(username.isEmpty()){
                username2ET.error = "Username required"
                username2ET.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).updateUsername(username).enqueue(object : Callback<ChangeUsernameResponse> {
                override fun onFailure(call: Call<ChangeUsernameResponse>, t: Throwable) {
                    println("[UsernameChangeScreen] FAILURE. Is the server running?" + t.stackTrace)
                }

                override fun onResponse(call: Call<ChangeUsernameResponse>, response: Response<ChangeUsernameResponse>) {
                    when {
                        response.code() == 200 -> {
                            println("[UsernameChangeScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            val intent = Intent(this@UsernameChangeScreen, MainSettingsScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        response.code() == 400 -> {
                            println("[UsernameChangeScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            val message = "This username is used by someone else."
                            val intent = Intent(this@UsernameChangeScreen, PopUpWindow::class.java)
                            intent.putExtra("popuptitle", "Error")
                            intent.putExtra("popuptext", message)
                            intent.putExtra("popupbtn", "OK")
                        }
                        response.code() == 401 -> {
                            println("[UsernameChangeScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        }
                    }
                }
            })
        }
    }
}