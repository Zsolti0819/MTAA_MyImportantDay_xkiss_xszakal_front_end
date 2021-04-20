package com.example.myimportantday.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.api.SessionManager
import com.example.myimportantday.models.EventList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var apiClient: APIclient
    private lateinit var sessionManager: SessionManager

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

        context?.let {
            apiClient.getApiService(it).getAllEvents()
                .enqueue(object : Callback<EventList> {
                    override fun onFailure(call: Call<EventList>, t: Throwable) {
                        Log.d("FAILURE, Obtained token", "${sessionManager.fetchAuthToken()}")
                    }

                    override fun onResponse(call: Call<EventList>, response: Response<EventList>) {
                        Log.d("SUCCESS, Obtained token", "${sessionManager.fetchAuthToken()}")
                        Log.d("TEXT", response.toString())

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


                        val adapter = MyAdapter(context!!, subjects, dates, places, priorities, advances, pics)

                        val listView = root.findViewById<ListView>(R.id.listView)
                        listView.adapter = adapter


                    }
                })
        }

        return root
    }

    class MyAdapter(private val context: Context, private val subjects: Array<String?>, private val dates: Array<String?>, private val places: Array<String?>, private val priorities: Array<String?>, private val advances: Array<String?>, private val pics: Array<String?>) : BaseAdapter() {
        private lateinit var subject: TextView
        private lateinit var date: TextView
        private lateinit var place: TextView
        private lateinit var priority: TextView
        private lateinit var advanced: TextView
        private lateinit var pic: TextView





        override fun getCount(): Int {
            return subjects.size
        }
        override fun getItem(position: Int): Any {
            return position
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val thisView = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
            subject = thisView.findViewById(R.id.subject)
            date = thisView.findViewById(R.id.date)
            place = thisView.findViewById(R.id.place)
            priority = thisView.findViewById(R.id.priority)
            advanced = thisView.findViewById(R.id.advanced)
            pic = thisView.findViewById(R.id.pic)

            subject.text = subjects[position]
            println(subject.text)
            date.text = dates[position]
            println(date.text)
            place.text = places[position]
            println(place.text)
            priority.text = priorities[position]
            println(priority.text)
            advanced.text = advances[position]
            println(advanced.text)
            pic.text = pics[position]
            println(pic.text)

            return thisView
        }
    }


}