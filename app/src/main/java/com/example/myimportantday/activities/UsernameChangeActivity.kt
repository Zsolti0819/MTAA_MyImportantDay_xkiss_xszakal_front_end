package com.example.myimportantday.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.ChangeUsernameResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_username_change.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsernameChangeActivity : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username_change)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your Username"
        actionBar.setDisplayHomeAsUpEnabled(true)

        changeUsernameButton.setOnClickListener {

            val username = usernameField.text.toString().trim()

            if(username.isEmpty()){
                editUsername.error = "Username required"
                editUsername.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).updateUsername(username)
                .enqueue(object : Callback<ChangeUsernameResponse> {
                    override fun onFailure(call: Call<ChangeUsernameResponse>, t: Throwable) {
                        println("FAILURE. Could not log in the user. Is the server running?")
                    }

                    override fun onResponse(call: Call<ChangeUsernameResponse>, response: Response<ChangeUsernameResponse>) {

                        if (response.code() == 200) {
                            println(response.body())
                            val intent = Intent(this@UsernameChangeActivity, SettingsActivity::class.java)
                            finish()
                            startActivity(intent)
                        }
                    }
                })
        }
    }
}