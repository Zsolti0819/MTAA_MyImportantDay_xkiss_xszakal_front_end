package com.example.myimportantday.ui.newEvent

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventPost
import com.example.myimportantday.tools.*
import kotlinx.android.synthetic.main.fragment_new_event.*
import kotlinx.android.synthetic.main.fragment_new_event.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class NewEventFragment : Fragment() {
    lateinit var sessionManager: SessionManager
    private lateinit var apiClient: APIclient
    private lateinit var filePath: Uri
    private var code: Int = 0

    @RequiresApi(Build.VERSION_CODES.KITKAT)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_event, container, false)

        apiClient = APIclient()
        sessionManager = context?.let { SessionManager(it) }!!


        root.photoButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 123)
        }

        root.createEventButton.setOnClickListener {

            val eventSubject = subjectET.text.toString().trim()
            val eventDate = dateET.text.toString().trim()
            val eventPlace = placeET.text.toString().trim()
            val eventPriority = priorityET.text.toString().trim()
            val eventAdvanced = advancedET.text.toString().trim()

            if(eventSubject.isEmpty()){
                subjectET.error = "Subject is required"
                subjectET.requestFocus()
                return@setOnClickListener
            }

            if(eventDate.isEmpty()){
                dateET.error = "Date is required"
                dateET.requestFocus()
                return@setOnClickListener
            }

            if(eventPlace.isEmpty()){
                placeET.error = "Place is required"
                placeET.requestFocus()
                return@setOnClickListener
            }

            if(eventPriority.isEmpty()){
                priorityET.error = "Priority is required"
                priorityET.requestFocus()
                return@setOnClickListener
            }

            if (code == 0) {
                photoButton.error = "Photo required"
                photoButton.requestFocus()
                return@setOnClickListener
            }

            val parcelFileDescriptor =
                requireContext().contentResolver.openFileDescriptor(filePath, "r", null)
                    ?: return@setOnClickListener
            val file = File(requireContext().contentResolver.getFileName(filePath))
            println(file)
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)
            val requestFile: RequestBody = create("multipart/form-data".toMediaTypeOrNull(), file)
            val body: MultipartBody.Part = createFormData("photo", file.name, requestFile)

            context?.let {
                apiClient.getApiService(it).postEvent(eventSubject, eventDate, eventPlace, eventPriority, eventAdvanced, body).enqueue(object : Callback<EventPost> {
                    override fun onResponse(call: Call<EventPost>, response: Response<EventPost>) {
                        println("SUCCESS")
                    }

                    override fun onFailure(call: Call<EventPost>, t: Throwable) {
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
            println("data.data === "+ data.data)

            //photo_file.setImageURI(data!!.getData()!!)
            photoButton.text = filePath.toString()
        }
    }

    fun ContentResolver.getFileName(uri: Uri): String {

        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return name
    }


}

