package com.example.myimportantday

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.Constants
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import com.example.myimportantday.tools.PopUpWindow
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_edit_event.*
import kotlinx.android.synthetic.main.activity_edit_event.advancedET
import kotlinx.android.synthetic.main.activity_edit_event.datePicker
import kotlinx.android.synthetic.main.activity_edit_event.placeET
import kotlinx.android.synthetic.main.activity_edit_event.prioritySP
import kotlinx.android.synthetic.main.activity_edit_event.subjectET
import kotlinx.android.synthetic.main.activity_edit_event.timePicker
import kotlinx.android.synthetic.main.fragment_new_event.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditEvent : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient
    lateinit var eventPriority: String
    private lateinit var eventDate: String
    private lateinit var eventTime: String

    @RequiresApi(Build.VERSION_CODES.O)
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
                val outputFormatterDate: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy")
                val formattedDate: String = outputFormatterDate.format(date)
                val outputFormatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formattedTime: String = outputFormatterTime.format(time)


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
                eventDate = formattedDate
                eventTime = formattedTime
                //set place
                placeET.setText(event?.place)
                //set priority
                showPrioritySpinner(response.body())
                //set advanced
                advancedET.setText(event?.advanced)
                val path = Constants.BASE_URL.plus(event?.pic)

            }
        })

        saveEdit.setOnClickListener{
            if (formDataThenPost(this,eventID)) return@setOnClickListener
        }

        // Datepicker
        datePicker.setOnDateChangedListener { _, year, month, day ->
            val selectedDate = "${year}-${month + 1}-${day}"
            eventDate = selectedDate
            println("User selected eventDate: $eventDate")
        }
        // Timepicker
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            val selectedTime = "${hour}:${minute}"
            eventTime = selectedTime
            println("User selected eventTime: $eventTime")
        }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDateAndTime(root: View) {
        // Datepicker
        datePicker.setOnDateChangedListener { _, year, month, day ->
            val selectedDate = "${year}-${month + 1}-${day}"
            eventDate = selectedDate
            println("User selected eventDate: $eventDate")
        }
        // Timepicker
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            val selectedTime = "${hour}:${minute}"
            eventTime = selectedTime
            println("User selected eventTime: $eventTime")
        }
    }

    private fun formDataThenPost(root: EditEvent, eventID:Int): Boolean {
        val eventSubject = subjectET.text.toString().trim()
        val eventDateAndTime = eventDate.plus("T").plus(eventTime)
        val eventPlace = placeET.text.toString().trim()
        val eventAdvanced = advancedET.text.toString().trim()

        if (eventSubject.isEmpty()) {
            subjectET.error = "Subject is required"
            subjectET.requestFocus()
            return true
        }

        if (eventPlace.isEmpty()) {
            placeET.error = "Place is required"
            placeET.requestFocus()
            return true
        }


        // Final data
        val subject: RequestBody = eventSubject.toRequestBody(MultipartBody.FORM)
        val date: RequestBody = eventDateAndTime.toRequestBody(MultipartBody.FORM)
        val place: RequestBody = eventPlace.toRequestBody(MultipartBody.FORM)
        val priority: RequestBody = eventPriority.toRequestBody(MultipartBody.FORM)
        val advanced: RequestBody = eventAdvanced.toRequestBody(MultipartBody.FORM)



        apiClient.getApiService(this).updateEvent(eventID,subject, date, place, priority, advanced,null)
            .enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            println("[NewEventFragment] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("[POST] subject = $eventSubject")
                            println("[POST] date = $eventDateAndTime")
                            println("[POST] place = $eventPlace")
                            println("[POST] priority = $eventPriority")
                            println("[POST] advanced = $eventAdvanced")
                            val message = "The event '$eventSubject' was successfully created.\n You can find it in the calendar under $eventDate or by viewing all your events."

//                            val intent = Intent(this@EditEvent, PopUpWindow::class.java)
//                            intent.putExtra("popuptitle", "Success")
//                            intent.putExtra("popuptext", message)
//                            intent.putExtra("popupbtn", "OK")
//                            intent.putExtra("nextActivity", "MainScreen")
//                            startActivity(intent)

                        }
                        response.code() == 400 -> {
                            println("[EditEvent] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("This is not possible")
                            val message = "Something went terrible wrong ... "
//                            Snackbar.make(this@EditEvent, message, Snackbar.LENGTH_LONG).also { snackbar -> snackbar.duration = 5000 }.show()
                        }
                        response.code() == 401 -> {
                            println("[EditEvent] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("This can only happen if the database was erased, but in the app the sessionManager still stores the user's token. In this case the user is redirected to the Login screen, and the token from the SessionManager will be deleted.")
                            println("[EditEvent] INFO. Pre-Token ${sessionManager.fetchAuthToken()}.")
                            sessionManager.deleteTokens()
                            println("[EditEvent] INFO. Post-Token ${sessionManager.fetchAuthToken()}.")
                            val intent = Intent(this@EditEvent, LoginScreen::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        response.code() == 404 -> {
                            println("[EditEvent] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("This is not possible")
                            val message = "Something went terrible wrong ... "
//                            Snackbar.make(this@EditEvent, message, Snackbar.LENGTH_LONG).also { snackbar -> snackbar.duration = 5000 }.show()
                        }
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    println("[EditEvent] FAILURE. Token ${sessionManager.fetchAuthToken()}. Error: " + t.message)

                }
            })
        return false
    }

}