package com.example.myimportantday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.Constants
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.DeleteResponse
import com.example.myimportantday.models.EventResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_single_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleEvent : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_event)

        val eventID:Int = intent.getIntExtra("id",0)

        apiClient = APIclient()
        sessionManager = SessionManager(this)

        apiClient.getApiService(this).showEventByID(eventID).enqueue(object : Callback<EventResponse>{
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                println("[SingleEvent] Failure. Error" + t.stackTrace)
            }
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                println("[SingleEvent] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                val event = response.body()


                subject.text = event?.subject
                date.text = event?.date
                place.text = event?.place
                priority.text = event?.priority
                advanced.text = event?.advanced
                val path = Constants.BASE_URL.plus(event?.pic)
                Picasso.with(this@SingleEvent).load(path).into(pic)
            }

        })


        editButton.setOnClickListener{
            val intent = Intent(this, EditEvent::class.java)
            intent.putExtra("id",eventID);
            startActivity(intent)
        }

        deleteButton.setOnClickListener{
            intent = Intent(this, MainScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            apiClient.getApiService(this).deleteEvent(eventID).enqueue(object : Callback<DeleteResponse> {
                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Log.d("Error", "Response: " + t.message)
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<DeleteResponse>,
                    response: Response<DeleteResponse>
                ) {
                    Toast.makeText(applicationContext, "Your event was deleted!", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            })
        }

    }
}