package com.example.myimportantday.ui.newEvent

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
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
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.example.myimportantday.LoginScreen
import com.example.myimportantday.MainScreen
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import com.example.myimportantday.tools.*
import com.example.myimportantday.ui.home.HomeFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_logged_in_screen.*
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
    private var filePath: Uri? = null
    private lateinit var eventDate: String
    private lateinit var eventTime: String
    private var picture: MultipartBody.Part? = null
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

        getDateAndTime(root)
        getPriority(root)

        // Select a photo
        root.photoButton.setOnClickListener{
            uploadImage()
        }

        // Create the event
        root.createEventButton.setOnClickListener {
            if (formDataThenPost(root)) return@setOnClickListener
        }
        return root
    }

    private fun getPriority(root: View) {
        // Priority Spinner
        val priorityList = resources.getStringArray(R.array.priority_levels)
        val prioritySpinner = root.findViewById<Spinner>(R.id.prioritySP)
        var pos: Int
        prioritySpinner.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            priorityList)
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
                pos = 1
                eventPriority = priorityList[pos]
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDateAndTime(root: View) {
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
            val selectedDate = "${year}-${month + 1}-${day}"
            eventDate = selectedDate
            println("User selected eventDate: $eventDate")
        }


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
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun formDataThenPost(root: View): Boolean {
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
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(
                filePath!!,
                "r",
                null) ?: return true
            val file = File(requireContext().cacheDir, requireContext().contentResolver.getFileName(
                filePath!!))
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            picture = createFormData("pic", file.name, requestFile)

        }

        context?.let {
            apiClient.getApiService(it).postEvent(subject, date, place, priority, advanced, picture)
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
                                println("[POST] pic = $picture")
                                val message =
                                    "Event was created successfully!\nYou can find it in the calendar!"

//                                val intent = Intent(context, MainScreen::class.java)
//                                intent.flags = FLAG_ACTIVITY_CLEAR_TOP
//                                startActivity(intent)
                                Snackbar.make(root, message, Snackbar.LENGTH_LONG).also { snackbar -> snackbar.duration = 5000 }.show()

                            }
                            response.code() == 400 -> {
                                println("[UsernameChangeScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                                println("This is not possible")
                                val message = "Something went terrible wrong ... "
                                Snackbar.make(root, message, Snackbar.LENGTH_LONG)
                                    .also { snackbar -> snackbar.duration = 5000 }.show()
                            }
                            response.code() == 401 -> {
                                println("[UsernameChangeScreen] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                                println("This can only happen if the database was erased, but in the app the sessionManager still stores the user's token. In this case the user is redirected to the Login screen, and the token from the SessionManager will be deleted.")
                                println("[MainSettingsScreen] INFO. Pre-Token ${sessionManager.fetchAuthToken()}.")
                                sessionManager.deleteTokens()
                                println("[MainSettingsScreen] INFO. Post-Token ${sessionManager.fetchAuthToken()}.")
                                val intent = Intent(context, LoginScreen::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                        println("[NewEventFragment] FAILURE. Token ${sessionManager.fetchAuthToken()}.")

                    }
                })
        }
        return false
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
            filePath = data!!.data!!
            println("data.data: " + data.data)

            photoButton.text = "A photo was selected"
        }
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


}

