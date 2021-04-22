package com.example.myimportantday.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventList
import com.example.myimportantday.tools.EventListAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalMultiplatform
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        apiClient = APIclient()
        sessionManager = context?.let { SessionManager(it) }!!

        val currentDateTime = LocalDateTime.now()

        context?.let {
            apiClient.getApiService(it).showAllEvents(currentDateTime.format(DateTimeFormatter.ISO_DATE)).enqueue(object : Callback<EventList> {
                override fun onFailure(call: Call<EventList>, t: Throwable) {
                    println("[HomeFragment] FAILURE. Is the server running?" + t.stackTrace)
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
                    println("[HomeFragment] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())

                    val eventList = response.body()

                    if (eventList?.events?.size == 0) {
                        no_events.setVisibility(View.VISIBLE)
                    }

                    else if (eventList?.events?.size != 0) {
                        no_events.visibility = View.VISIBLE
                        when {
                            eventList?.events?.size == 1 -> no_events.text = "You have ${eventList.events.size} event planned for today. Easy."
                            eventList?.events?.size!! > 1 && eventList.events.size < 4 -> no_events.text = "You have ${eventList.events.size} events planned for today. You got this!"
                            else -> no_events.text = "You have ${eventList.events.size} events planned for today. What a busy day!"
                        }

                        val subjects = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            subjects[i] = eventList.events[i].subject

                        val dates = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            dates[i] = eventList.events[i].date.toString()

                        val places = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            places[i] = eventList.events[i].place

                        val priorities = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            priorities[i] = eventList.events[i].priority

                        val advances = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            advances[i] = eventList.events[i].advanced

                        val pics = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            pics[i] = eventList.events[i].pic

                        val adapter = EventListAdapter(context!!, subjects, dates, places, priorities, advances, pics)

                        val listView = root.findViewById<ListView>(R.id.listView)
                        listView.adapter = adapter
                    }
                }
            })
        }

        return root
    }

    }