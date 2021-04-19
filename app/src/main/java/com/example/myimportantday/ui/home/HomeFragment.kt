package com.example.myimportantday.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventsResponse
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager

    @ExperimentalMultiplatform
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        apiClient = APIclient()
        sessionManager = context?.let { SessionManager(it) }!!

        context?.let {
            apiClient.getApiService(it).getAllEvents()
                .enqueue(object : Callback<EventsResponse> {
                    override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                        Log.d("FAILURE, Token", "${sessionManager.fetchAuthToken()}")
                        Log.d("Textview", textView.text.toString())
                    }

                    override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
                        Log.d("RESPONSE, Token", "${sessionManager.fetchAuthToken()}")
                        textView.text = response.body().toString()
                    }
                })
        }

        return root
    }
}