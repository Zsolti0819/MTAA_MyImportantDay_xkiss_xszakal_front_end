package com.example.myimportantday.activities.loggedOut

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.activities.loggedIn.MainScreen
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.LoginResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginScreen : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        loginButton.setOnClickListener {

            val username = usernameET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if(username.isEmpty()){
                usernameET.error = "Username is required"
                usernameET.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                passwordET.error = "Password is required"
                passwordET.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).login(username, password).enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    println("[LoginScreen] FAILURE. Is the server running?" + t.stackTrace)
                }
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.code() == 200) {
                        println("[LoginScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        sessionManager.saveAuthToken(response.body()?.token!!)

                        val intent = Intent(applicationContext, MainScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    else if (response.code() == 400) {
                        println("[LoginScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        val message = "Wrong username or password"
                        Snackbar.make(it, message, Snackbar.LENGTH_LONG).also { snackbar -> snackbar.duration = 5000 }.show()
                    }
                }
            })
        }

        registerButton.setOnClickListener{
            val intent = Intent(applicationContext, RegisterScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}


