package com.example.myimportantday.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventList
import com.example.myimportantday.tools.EventListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarFragment : Fragment() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager

    @ExperimentalMultiplatform
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        apiClient = APIclient()
        sessionManager = context?.let { SessionManager(it) }!!
        val calendar = root.findViewById<CalendarView>(R.id.calendarView)
        calendar.setOnDateChangeListener { myCalendar, year, month, dayOfMonth ->
            println("Selected date $year/${month+1}/$dayOfMonth")
            context?.let {
                    apiClient.getApiService(it).showAllEvents()
                        .enqueue(object : Callback<EventList> {
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

                                val adapter = EventListAdapter(context!!,
                                    subjects,
                                    dates,
                                    places,
                                    priorities,
                                    advances,
                                    pics)

                                val listView = root.findViewById<ListView>(R.id.listView)
                                listView.adapter = adapter

                            }
                        })
                }
        }

        return root
    }
}