package com.example.myimportantday.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.R
import com.example.myimportantday.api.RetrofitClient
import com.example.myimportantday.model.LoginResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//      Ak klikneme na LOG IN
        login.setOnClickListener {

            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if(username.isEmpty()){
                editUsername.error = "Username required"
                editUsername.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                editPassword.error = "Password required"
                editPassword.requestFocus()
                return@setOnClickListener
            }

            RetrofitClient.INSTANCE.userLogin(username, password)
                .enqueue(object: Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        Log.d("Response", response.toString())
                        Log.d("Body response", response.body().toString())
                        val intent = Intent(applicationContext, ProfileActivity::class.java)
                        startActivity(intent)
//                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//                                startActivity(intent)

//                            if(!response.body()?.token!!){
//                                Log.d("WTF", "THIS")
//
//                                SharedPrefManager.getInstance(applicationContext).saveUser(response.body()?.user!!)
//
//                                val intent = Intent(applicationContext, ProfileActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//                                startActivity(intent)
//
//
//                            }else{
//                                Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
//                            }

                    }
                })

        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        if(SharedPrefManager.getInstance(this).isLoggedIn){
//            val intent = Intent(applicationContext, ProfileActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//            startActivity(intent)
//        }
//    }
}