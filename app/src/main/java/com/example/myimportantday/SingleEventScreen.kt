package com.example.myimportantday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.Constants
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.DeleteEventResponse
import com.example.myimportantday.models.EventResponse
import com.example.myimportantday.tools.PopUpWindow
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_single_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleEventScreen : AppCompatActivity() {

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
                println("[SingleEventScreen] Failure. Error" + t.stackTrace)
            }
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                println("[SingleEventScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                val event = response.body()


                subject.text = event?.subject
                date.text = event?.date
                place.text = event?.place
                priority.text = event?.priority
                advanced.text = event?.advanced
                val path = Constants.BASE_URL.plus(event?.pic)
                Picasso.with(this@SingleEventScreen).load(path).into(pic)
            }

        })


        editButton.setOnClickListener{
            val intent = Intent(this, EditEvent::class.java)
            intent.putExtra("id",eventID);
            startActivity(intent)
        }

        deleteButton.setOnClickListener{

            apiClient.getApiService(this).deleteEvent(eventID).enqueue(object : Callback<DeleteEventResponse> {
                override fun onFailure(call: Call<DeleteEventResponse>, t: Throwable) {
                    println("[SingleEventScreen] FAILURE. Token ${sessionManager.fetchAuthToken()}. Is the server running?" + t.stackTrace)
                }

                override fun onResponse(
                    call: Call<DeleteEventResponse>,
                    response: Response<DeleteEventResponse>
                ) {
                    val message = "The event was successfully deleted! \n You are being redirected to the Home screen."
                    val intent = Intent(applicationContext, PopUpWindow::class.java)
                    intent.putExtra("popuptitle", "Success")
                    intent.putExtra("popuptext", message)
                    intent.putExtra("popupbtn", "OK")
                    intent.putExtra("nextActivity", "MainScreen")
                    startActivity(intent)
                }
            })
        }

    }
}