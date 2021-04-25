package com.example.myimportantday

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.Constants
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import kotlinx.android.synthetic.main.activity_edit_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditEvent : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient
    lateinit var eventPriority: String

    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        val eventID:Int = intent.getIntExtra("id", 0)

        apiClient = APIclient()
        sessionManager = SessionManager(this)


        apiClient.getApiService(this).showEventByID(eventID).enqueue(object :
            Callback<EventResponse> {
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                println("[EditEvent] Failure. Error" + t.stackTrace)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                println("[EditEvent] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                val event = response.body()
                //val datetime:Date = event?.date.
                val inputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val date: LocalDate = LocalDate.parse(event?.date, inputFormatter)
                val time: LocalTime = LocalTime.parse(event?.date,inputFormatter)

                //set subject
                subjectET.setText(event?.subject)
                //set time
                timePicker.setIs24HourView(true)
                timePicker.hour = time.hour
                timePicker.minute = time.minute
                //set date
                val calendar = Calendar.getInstance()
                datePicker.minDate = calendar.timeInMillis
                datePicker.init(date.year, date.monthValue, date.dayOfMonth, null)
                //set place
                placeET.setText(event?.place)
                //set priority
                showPrioritySpinner(response.body())
                //set advanced
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
        prioritySpinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            priorityList)

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