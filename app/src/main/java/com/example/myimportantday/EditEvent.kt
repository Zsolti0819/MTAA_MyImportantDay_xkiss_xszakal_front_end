package com.example.myimportantday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.Constants
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditEvent : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        val eventID:Int = intent.getIntExtra("id",0)

        apiClient = APIclient()
        sessionManager = SessionManager(this)


        apiClient.getApiService(this).showEventByID(eventID).enqueue(object :
            Callback<EventResponse> {
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                println("[EditEvent] Failure. Error" + t.stackTrace)
            }
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                println("[EditEvent] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                val event = response.body()

                subjectET.setText(event?.subject)
                //datePicker.set = event?.date
                placeET.setText(event?.place)
                //prioritySP.se event?.priority
                advancedET.setText(event?.advanced)
                val path = Constants.BASE_URL.plus(event?.pic)
            }

        })

    }
}