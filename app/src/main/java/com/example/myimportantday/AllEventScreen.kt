package com.example.myimportantday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventList
import com.example.myimportantday.tools.EventListAdapter
import kotlinx.android.synthetic.main.activity_all_event_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllEventScreen : AppCompatActivity() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager
    lateinit var ids:Array<String?>
    var idEvent:Int = 0

    @ExperimentalMultiplatform
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_event_screen)

        apiClient = APIclient()
        sessionManager = SessionManager(this)

        apiClient.getApiService(this).showAllEvents().enqueue(object :
            Callback<EventList> {
            override fun onFailure(call: Call<EventList>, t: Throwable) {
                println("[AllEventScreen] FAILURE. Is the server running?" + t.stackTrace)
            }

            override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
                println("[AllEventScreen] SUCCESS. Token ${sessionManager.fetchAuthToken()}. Response: " + response.toString())

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

                ids = arrayOfNulls<String>(eventList.events.size)
                for (i: Int in eventList.events.indices)
                    ids[i] = (eventList.events[i].id).toString()

                val adapter = EventListAdapter(this@AllEventScreen,
                    subjects,
                    dates,
                    places,
                    priorities,
                    advances,
                    pics,
                    ids)

                listView.adapter = adapter
                if(eventList.events.isNotEmpty())
                    listView.setOnItemClickListener { _, _, position, _ ->

                        val idOfSelectedItem = ids[position]
                        println("Selected event ID: $idOfSelectedItem")
                        idEvent = idOfSelectedItem!!.toInt()

                        val intent = Intent(this@AllEventScreen, SingleEventScreen::class.java)
                        intent.putExtra("id", idEvent);
                        startActivity(intent)
                    }
            }
        })
    }
}