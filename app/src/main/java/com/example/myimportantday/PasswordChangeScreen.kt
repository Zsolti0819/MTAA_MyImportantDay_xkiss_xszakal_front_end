package com.example.myimportantday

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.ChangePasswordResponse
import kotlinx.android.synthetic.main.activity_password_change_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordChangeScreen : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change_screen)

        val actionBar = supportActionBar
        actionBar!!.title = "Change Your Password"
        actionBar.setDisplayHomeAsUpEnabled(true)
        changePasswordBTN.setOnClickListener {

            val oldPassword = oldPasswordET.text.toString().trim()
            val newPassword = newPasswordET.text.toString().trim()

            if(oldPassword.isEmpty()){
                oldPasswordET.error = "You must enter your old password!"
                oldPasswordET.requestFocus()
                return@setOnClickListener
            }

            if(newPassword.isEmpty()){
                newPasswordET.error = "You must enter your new password!"
                newPasswordET.requestFocus()
                return@setOnClickListener
            }

            apiClient = APIclient()
            sessionManager = SessionManager(this)

            apiClient.getApiService(this).updatePassword(oldPassword, newPassword).enqueue(object : Callback<ChangePasswordResponse> {
                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    println("[PasswordChangeActivity] FAILURE. Is the server running?" + t.stackTrace)
                }

                override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                    when {
                        response.code() == 200 -> {
                            println("[PasswordChangeActivity] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            val intent = Intent(this@PasswordChangeScreen, MainSettingsScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        response.code() == 400 -> {
                            println("[PasswordChangeActivity] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            Toast.makeText(this@PasswordChangeScreen, "Your old password is not correct!",Toast.LENGTH_LONG).show()
                        }
                        response.code() == 401 -> {
                            println("[PasswordChangeActivity] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        }
                    }
                }
            })
        }
    }
}