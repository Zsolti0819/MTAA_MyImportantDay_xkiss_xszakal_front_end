package com.example.myimportantday.activities.loggedIn.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.activities.loggedIn.AllEventScreen
import com.example.myimportantday.activities.loggedIn.SingleEventScreen
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventList
import com.example.myimportantday.tools.EventListAdapter
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager
    lateinit var ids:Array<String?>
    var idEvent:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalMultiplatform
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        apiClient = APIclient()
        sessionManager = context?.let { SessionManager(it) }!!

        val currentDateTime = LocalDate.now()

        root.showAllButton.setOnClickListener{
            val intent = Intent(requireContext(), AllEventScreen::class.java)
            startActivity(intent)
        }

        context?.let {
            apiClient.getApiService(it).showAllEventsByDate(currentDateTime.format(DateTimeFormatter.ISO_DATE)).enqueue(object : Callback<EventList> {
                override fun onFailure(call: Call<EventList>, t: Throwable) {
                    println("[CalendarFragment] FAILURE. Is the server running?" + t.stackTrace)
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
                    println("[CalendarFragment] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())

                    val eventList = response.body()
                    val subjects = arrayOfNulls<String>(eventList?.events!!.size)
                    for (i: Int in eventList.events.indices)
                        subjects[i] = eventList.events[i].subject

                    val dates = arrayOfNulls<String>(eventList.events.size)
                    for (i: Int in eventList.events.indices)
                        dates[i] = eventList.events[i].date

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

                    ids = arrayOfNulls(eventList.events.size)
                    for (i: Int in eventList.events.indices)
                        ids[i] = (eventList.events[i].id).toString()

                    val adapter = EventListAdapter(context!!,
                        subjects,
                        dates,
                        places,
                        priorities,
                        advances,
                        pics)

                    val listView = root.findViewById<ListView>(R.id.listView)
                    listView.adapter = adapter
                    if(eventList.events.isNotEmpty())
                        listView.setOnItemClickListener { _, _, position, _ ->

                            val idOfTheSelectedEvent = ids[position]
                            println("[CalendarFragment] INFO. Selected event from the list: $idOfTheSelectedEvent")

                            idEvent = idOfTheSelectedEvent!!.toInt()

                            val intent = Intent(requireContext(), SingleEventScreen::class.java)
                            intent.putExtra("id", idEvent)
                            startActivity(intent)
                        }

                }
            })
        }

        val calendar = root.findViewById<CalendarView>(R.id.calendarView)
        calendar.setOnDateChangeListener { _, year, month, day ->
            val selectedDate = "$year-${month+1}-$day"
            context?.let {
                apiClient.getApiService(it).showAllEventsByDate(selectedDate).enqueue(object : Callback<EventList> {
                    override fun onFailure(call: Call<EventList>, t: Throwable) {
                        println("[CalendarFragment] FAILURE. Is the server running?" + t.stackTrace)
                    }

                    override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
                        println("[CalendarFragment] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())

                        val eventList = response.body()
                        val subjects = arrayOfNulls<String>(eventList?.events!!.size)
                        for (i: Int in eventList.events.indices)
                            subjects[i] = eventList.events[i].subject

                        val dates = arrayOfNulls<String>(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            dates[i] = eventList.events[i].date

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

                        ids = arrayOfNulls(eventList.events.size)
                        for (i: Int in eventList.events.indices)
                            ids[i] = (eventList.events[i].id).toString()

                        val adapter = EventListAdapter(context!!, subjects, dates, places, priorities, advances, pics)

                        val listView = root.findViewById<ListView>(R.id.listView)
                        listView.adapter = adapter
                        if (eventList.events.isNotEmpty()) {
                            listView.setOnItemClickListener { _, _, position, _ ->
                                val idOfTheSelectedEvent = ids[position]
                                println("[CalendarFragment] INFO. Selected event from the list: $idOfTheSelectedEvent")

                                idEvent = idOfTheSelectedEvent!!.toInt()

                                val intent = Intent(requireContext(), SingleEventScreen::class.java)
                                intent.putExtra("id", idEvent)
                                startActivity(intent)
                            }
                        }
                    }
                })
            }
        }

        return root
    }
}