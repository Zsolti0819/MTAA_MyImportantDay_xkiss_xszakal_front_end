package com.example.myimportantday.activities.loggedIn

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.activities.loggedOut.LoginScreen
import com.example.myimportantday.models.EventResponse
import com.example.myimportantday.tools.PopUpWindow
import kotlinx.android.synthetic.main.activity_edit_event.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
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

class EditEventScreen : AppCompatActivity() {

    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient
    lateinit var eventPriority: String
    private lateinit var eventDate: String
    private lateinit var eventTime: String
    private var filePath: Uri? = null
    private var picture: MultipartBody.Part? = null
    private var code: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        val eventID:Int = intent.getIntExtra("id", 0)

        apiClient = APIclient()
        sessionManager = SessionManager(this)

        val actionBar = supportActionBar
        actionBar!!.title = "Edit event"

        apiClient.getApiService(this).showEventByID(eventID).enqueue(object :
            Callback<EventResponse> {
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                println("[EditEventScreen] FAILURE. Error" + t.stackTrace)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                println("[EditEventScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                val event = response.body()

                // Subject
                subjectET.setText(event?.subject)

                // Date
                val inputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val date: LocalDate = LocalDate.parse(event?.date, inputFormatter)
                val outputFormatterDate: DateTimeFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd")
                val formattedDate: String = outputFormatterDate.format(date)
                println("[EditEventScreen] INFO. Retrieved date from the event: $formattedDate")
                eventDate = formattedDate

                val calendar = Calendar.getInstance()
                datePicker.minDate = calendar.timeInMillis
                datePicker.init(date.year, date.monthValue-1, date.dayOfMonth) { _, year, month, day ->
                    val selectedDate = "${year}-${month + 1}-${day}"
                    eventDate = selectedDate
                    println("[EditEventScreen] INFO. User selected eventDate: $eventDate")
                }

                // Time
                val time: LocalTime = LocalTime.parse(event?.date,inputFormatter)
                val outputFormatterTime = DateTimeFormatter.ofPattern("HH:mm:ss")
                val formattedTime: String = outputFormatterTime.format(time)
                println("[EditEventScreen] INFO. Retrieved time from the event: $formattedTime")
                eventTime = formattedTime


                timePicker.setIs24HourView(true)
                timePicker.hour = time.hour
                timePicker.minute = time.minute
                timePicker.setOnTimeChangedListener { _, hour, minute ->
                    val selectedTime = "${hour}:${minute}"
                    eventTime = selectedTime
                    println("[EditEventScreen] INFO. User selected eventTime: $eventTime")
                }

                // Place
                placeET.setText(event?.place)

                // Priority
                showPrioritySpinner(response.body())

                // Advanced
                advancedET.setText(event?.advanced)

                // Photo
                photoButton.setOnClickListener{
                    uploadImage()
                }
            }
        })

        saveEdit.setOnClickListener{
            if (formDataThenPUT(eventID)) return@setOnClickListener
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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun formDataThenPUT(eventID:Int): Boolean {
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

        // Photo
        if (filePath != null) {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(
                filePath!!,
                "r",
                null) ?: return true
            val file = File(cacheDir, contentResolver.getFileName(
                filePath!!))
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            picture = MultipartBody.Part.createFormData("pic", file.name, requestFile)

        }



        apiClient.getApiService(this).updateEvent(eventID,subject, date, place, priority, advanced,picture)
            .enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            println("[EditEventScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("[EditEventScreen] [PUT] subject = $eventSubject")
                            println("[EditEventScreen] [PUT] date = $eventDateAndTime")
                            println("[EditEventScreen] [PUT] place = $eventPlace")
                            println("[EditEventScreen] [PUT] priority = $eventPriority")
                            println("[EditEventScreen] [PUT] advanced = $eventAdvanced")

                            val message = "The event '$eventSubject' was successfully updated.\n You can find it in the calendar under $eventDate or by viewing all your events."
                            val intent = Intent(this@EditEventScreen, PopUpWindow::class.java)
                            intent.putExtra("popuptitle", "Success")
                            intent.putExtra("popuptext", message)
                            intent.putExtra("popupbtn", "OK")
                            intent.putExtra("nextActivity", "MainScreen")
                            startActivity(intent)

                        }
                        response.code() == 400 -> {
                            println("[EditEventScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("[EditEventScreen] INFO. This is not possible.")
                        }
                        response.code() == 401 -> {
                            println("[EditEventScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("[EditEventScreen] INFO.This can only happen if the database was erased, but in the app the sessionManager still stores the user's token. In this case the user is redirected to the Login screen, and the token from the SessionManager will be deleted.")
                            println("[EditEventScreen] INFO. Pre-Token ${sessionManager.fetchAuthToken()}.")
                            sessionManager.deleteTokens()
                            println("[EditEventScreen] INFO. Post-Token ${sessionManager.fetchAuthToken()}.")
                            val intent = Intent(this@EditEventScreen, LoginScreen::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        response.code() == 404 -> {
                            println("[EditEventScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                            println("[EditEventScreen] INFO.This is not possible.")
                        }
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    println("[EditEventScreen] FAILURE. Token ${sessionManager.fetchAuthToken()}. Error: " + t.message)

                }
            })
        return false
    }

    private fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 123)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            code = requestCode
            if (data != null) {
                filePath = data.data!!
                println("[EditEventScreen] INFO. filePath: " + data.data)
                photoButton.text = "A photo was selected."
            }
            else {
                filePath = null
                println("[EditEventScreen] INFO. filePath: " + data?.data)
                photoButton.text = "No photo will be added."
            }
        }
    }

}