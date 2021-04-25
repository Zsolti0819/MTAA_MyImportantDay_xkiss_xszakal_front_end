package com.example.myimportantday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.Constants
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import kotlinx.android.synthetic.main.activity_edit_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EditEvent : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient
    lateinit var eventPriority: String

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
                timePicker.setIs24HourView(true) // 24 órás formátum

                val calendar = Calendar.getInstance()
                datePicker.minDate = calendar.timeInMillis // ez csak arra van, hogy ne tudjon "tegnapra" létrehozni eventet

                placeET.setText(event?.place)

                showPrioritySpinner(response.body()) // ugyan az mint a create eventben, csak alapból állítsa be arra a priorityt, ami az előző eventnek volt. Azért teszteld majd le te is.

                advancedET.setText(event?.advanced)
                val path = Constants.BASE_URL.plus(event?.pic)

            }
        })
    }

    private fun showPrioritySpinner(event: EventResponse?) {
        // Priority Spinner
        val priorityList = resources.getStringArray(R.array.priority_levels)
        val prioritySpinner = findViewById<Spinner>(R.id.prioritySP)
        var pos: Int
        prioritySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityList)

        when {
            event?.priority.equals("Normal") -> prioritySP.setSelection(0)
            event?.priority.equals("Important") -> prioritySP.setSelection(1)
            else -> prioritySP.setSelection(2)
        }

        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                pos = position
                eventPriority = priorityList[pos]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                eventPriority = when {
                    event?.priority.equals("Normal") -> priorityList[0]
                    event?.priority.equals("Important") -> priorityList[1]
                    else -> priorityList[2]
                }
            }
        }
    }

}