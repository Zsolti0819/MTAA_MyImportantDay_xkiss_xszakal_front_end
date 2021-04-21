package com.example.myimportantday.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.ChangeEmailAddressResponse
import com.example.myimportantday.models.ChangeUsernameResponse
import kotlinx.android.synthetic.main.activity_email_addres_change.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_username_change.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailChangeActivity : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_addres_change)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your E-mail address"
        actionBar.setDisplayHomeAsUpEnabled(true)

        changeEmailButton.setOnClickListener {

            val email = emailField.text.toString().trim()

            if(email.isEmpty()){
                editUsername.error = "Email required"
                editUsername.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).updateEmailAddress(email)
                .enqueue(object : Callback<ChangeEmailAddressResponse> {
                    override fun onFailure(call: Call<ChangeEmailAddressResponse>, t: Throwable) {
                        println("FAILURE. Could not log in the user. Is the server running?")
                    }

                    override fun onResponse(call: Call<ChangeEmailAddressResponse>, response: Response<ChangeEmailAddressResponse>) {

                        if (response.code() == 200) {
                            println(response.body())
                            val intent = Intent(this@EmailChangeActivity, SettingsActivity::class.java)
                            finish()
                            startActivity(intent)
                        }
                    }
                })
        }
    }
}