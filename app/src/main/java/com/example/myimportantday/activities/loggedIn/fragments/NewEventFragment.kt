package com.example.myimportantday.activities.loggedIn.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.myimportantday.activities.loggedOut.LoginScreen
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventResponse
import com.example.myimportantday.tools.*
import com.google.android.material.snackbar.Snackbar
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
import java.util.*


class NewEventFragment : Fragment() {
    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient

    private lateinit var eventDate: String
    private lateinit var eventTime: String
    private var eventDateAndTime: String = "Not set"
    private lateinit var eventYear: String
    private lateinit var eventMonth: String
    private lateinit var eventDay: String
    private lateinit var eventHour: String
    private lateinit var eventMinute: String

    lateinit var eventPriority: String
    private var filePath: Uri? = null
    private var picture: MultipartBody.Part? = null
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
        root.dateAndTimeButton.setOnClickListener {
            setDateAndTime()
        }

        // Place
        setPriority(root)

        // Photo
        root.photoButton.setOnClickListener{
            uploadImage()
        }

        // Create the event
        root.createEventButton.setOnClickListener {
            if (formDataThenPOST(root)) return@setOnClickListener
        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateAndTime() {

        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    eventYear = year.toString()
                    this.set(Calendar.MONTH, month)
                    eventMonth = (month+1).toString()
                    this.set(Calendar.DAY_OF_MONTH, day)
                    eventDay = day.toString()
                    eventDate = "${eventYear}-${eventMonth}-${eventDay}"
                    println("[NewEventFragment] INFO. eventDate: $eventDate")
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            eventHour = hour.toString()
                            this.set(Calendar.MINUTE, minute)
                            eventMinute = minute.toString()
                            eventTime = "${eventHour}:${eventMinute}"
                            println("[NewEventFragment] INFO. eventTime: $eventTime")
                            eventDateAndTime = eventDate.plus("T").plus(eventTime)
                            println("[NewEventFragment] INFO. eventDateAndTime: $eventDateAndTime")
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setPriority(root: View) {
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

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 123)
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

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            code = requestCode
            if (data != null) {
                filePath = data.data!!
                println("[NewEventFragment] filePath: " + data.data)
                photoButton.text = "A photo was selected."
            }
            else {
                filePath = null
                println("[NewEventFragment] filePath: " + data?.data)
                photoButton.text = "You decided to not select a photo."
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun formDataThenPOST(root: View): Boolean {
        val eventSubject = subjectET.text.toString().trim()
        val eventDateAndTime = eventDateAndTime
        val eventPlace = placeET.text.toString().trim()
        val eventAdvanced = advancedET.text.toString().trim()

        if (eventSubject.isEmpty()) {
            subjectET.error = "Required"
            subjectET.requestFocus()
            return true
        }

        if (eventPlace.isEmpty()) {
            placeET.error = "Required"
            placeET.requestFocus()
            return true
        }

        if(eventDateAndTime == "Not set"){
            dateAndTimeButton.error = "Required"
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
                                println("[NewEventFragment] [POST] subject = $eventSubject")
                                println("[NewEventFragment] [POST] date = $eventDateAndTime")
                                println("[NewEventFragment] [POST] place = $eventPlace")
                                println("[NewEventFragment] [POST] priority = $eventPriority")
                                println("[NewEventFragment] [POST] advanced = $eventAdvanced")
                                println("[NewEventFragment] [POST] pic = $picture")
                                val message = "The event '$eventSubject' was successfully created.\n You can find it in the calendar under $eventDate or by viewing all your events."

                                val intent = Intent(context, PopUpWindow::class.java)
                                intent.putExtra("popuptitle", "Success")
                                intent.putExtra("popuptext", message)
                                intent.putExtra("popupbtn", "OK")
                                intent.putExtra("nextActivity", "MainScreen")
                                startActivity(intent)

                            }
                            response.code() == 400 -> {
                                println("[NewEventFragment] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                                println("[NewEventFragment] INFO. This is not possible")
                                val message = "Something went terrible wrong ... "
                                Snackbar.make(root, message, Snackbar.LENGTH_LONG).also { snackbar -> snackbar.duration = 5000 }.show()
                            }
                            response.code() == 401 -> {
                                println("[NewEventFragment] INFO. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())
                                println("[NewEventFragment] INFO. This can only happen if the database was erased, but in the app the sessionManager still stores the user's token. In this case the user is redirected to the Login screen, and the token from the SessionManager will be deleted.")
                                println("[NewEventFragment] INFO. Pre-Token ${sessionManager.fetchAuthToken()}.")
                                sessionManager.deleteTokens()
                                println("[NewEventFragment] INFO. Post-Token ${sessionManager.fetchAuthToken()}.")
                                val intent = Intent(context, LoginScreen::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
}

