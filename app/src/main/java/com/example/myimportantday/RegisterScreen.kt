package com.example.myimportantday

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.RegisterResponse
import kotlinx.android.synthetic.main.activity_register_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterScreen : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        loginBTN.setOnClickListener{
            val intent = Intent(applicationContext, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        registerBTN.setOnClickListener {

            val username = usernameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if(username.isEmpty()){
                usernameET.error = "Username is required"
                usernameET.requestFocus()
                return@setOnClickListener
            }

            if(email.isEmpty()){
                emailET.error = "E-mail is required"
                emailET.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                passwordET.error = "Password is required"
                passwordET.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).register(username, email, password).enqueue(object :
                Callback<RegisterResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    println("[RegisterScreen] FAILURE. Is the server running?" + t.stackTrace)
                }
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.code() == 200) {
                        println("[RegisterScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())

                        val intent = Intent(applicationContext, LoginScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    else if (response.code() == 400) {
                        println("[RegisterScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        Toast.makeText(applicationContext,"Wrong username or e-mail", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}