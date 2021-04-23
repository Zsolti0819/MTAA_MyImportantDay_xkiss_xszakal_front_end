package com.example.myimportantday.ui.newEvent

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import com.example.myimportantday.tools.*
import kotlinx.android.synthetic.main.fragment_new_event.*
import kotlinx.android.synthetic.main.fragment_new_event.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
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
import java.util.*


class NewEventFragment : Fragment() {
    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient
    private lateinit var filePath: Uri
    private lateinit var eventDate: String
    private lateinit var eventTime: String
    lateinit var eventPriority: String
    private var code: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_event, container, false)

        apiClient = APIclient()
        sessionManager = context?.let { SessionManager(it) }!!


        // Date
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue
        val currentDay = LocalDate.now().dayOfMonth
        val currentDate = "${currentYear}-${currentMonth}-${currentDay}"
        eventDate = currentDate
        println("Untouched eventDate: $eventDate")

        // Datepicker
        val datePicker = root.findViewById<DatePicker>(R.id.datePicker)
        datePicker.setOnDateChangedListener { _, year, month, day ->
            val selectedDate = "${year}-${month+1}-${day}"
            eventDate = selectedDate
            println("User selected eventDate: $eventDate")}


        // Time
        val currentHour = LocalTime.now().hour
        val currentMinutes = LocalTime.now().minute
        val currentTime = "${currentHour}:${currentMinutes}"
        eventTime = currentTime
        println("Untouched eventTime: $eventTime")

        // Timepicker
        val timePicker = root.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            val selectedTime = "${hour}:${minute}"
            eventTime = selectedTime
            println("User selected eventTime: $eventTime")
        }


        // Priority Spinner
        val priorityList = resources.getStringArray(R.array.priority_levels)
        val prioritySpinner = root.findViewById<Spinner>(R.id.prioritySP)
        var pos: Int
        prioritySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityList)
        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)  {
                pos = position
                eventPriority = priorityList[pos]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                pos = 1
                eventPriority = priorityList[pos]
            }
        }

        // Photo Button
        root.photoButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 123)
        }

        // Create Event Button
        root.createEventButton.setOnClickListener {

            val eventSubject = subjectET.text.toString().trim()
            val eventPlace = placeET.text.toString().trim()

            val eventAdvanced = advancedET.text.toString().trim()

            if(eventSubject.isEmpty()){
                subjectET.error = "Subject is required"
                subjectET.requestFocus()
                return@setOnClickListener
            }

            if(eventPlace.isEmpty()){
                placeET.error = "Place is required"
                placeET.requestFocus()
                return@setOnClickListener
            }

            // Photo
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(filePath, "r", null) ?: return@setOnClickListener
            val file = File(requireContext().cacheDir, requireContext().contentResolver.getFileName(filePath))
            println("file:$file")
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            val pic: MultipartBody.Part = createFormData("pic", file.name, requestFile)
            println("filePart: $pic")

            val eventDateAndTime = eventDate.plus("T").plus(eventTime)
            println("eventDateAndTime: $eventDateAndTime")

            val subject: RequestBody = eventSubject.toRequestBody(MultipartBody.FORM)
            val date: RequestBody = eventDateAndTime.toRequestBody(MultipartBody.FORM)
            val place: RequestBody = eventPlace.toRequestBody(MultipartBody.FORM)
            val priority: RequestBody = eventPriority.toRequestBody(MultipartBody.FORM)
            val advanced: RequestBody = eventAdvanced.toRequestBody(MultipartBody.FORM)


            context?.let {
                apiClient.getApiService(it).postEvent(subject,
                    date,
                    place,
                    priority,
                    advanced,
                    pic).enqueue(object : Callback<EventResponse> {
                    override fun onResponse(
                        call: Call<EventResponse>,
                        response: Response<EventResponse>
                    ) {
                        println("[NewEventFragment] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                        println("SUCCESS")
                        println("eventSubject = $eventSubject")
                        println("eventDateAndTime = $eventDateAndTime")
                        println("eventPlace = $eventPlace")
                        println("eventPriority = $eventPriority")
                        println("eventAdvanced = $eventAdvanced")
                        println("pic = $pic")

                    }

                    override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                        println("FAIL")

                    }
                })
            }
        }
        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            code = requestCode
            filePath = data!!.data!!
            println("data.data: " + data.data)

            photoButton.text = filePath.toString()
        }
    }

    private fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            println("name: $name")
        }
        return name
    }


}

