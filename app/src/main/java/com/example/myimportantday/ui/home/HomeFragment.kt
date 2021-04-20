package com.example.myimportantday.ui.home

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
import com.google.gson.GsonBuilder
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

                        val body = response.body().toString()
                        Log.d("R.BODY", body)

                        val listView = root.findViewById<ListView>(R.id.listView)
                        listView.adapter =
                            response.body()?.let { it1 -> MyCustomAdapter(it1, context!!) }

                    }
                })
        }

        return root
    }


    private class MyCustomAdapter(val eventList: EventList, context: Context): BaseAdapter() {

        private val mContext: Context = context

        override fun getCount(): Int {
            return eventList.events.size
        }

        override fun getItem(position: Int): Any {
            return "Test string"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val textView = TextView(mContext)
            for (i in 0 until eventList.events.size)
                Log.d("event[].subject", eventList.events[i].subject)
            textView.text = eventList.events[5].subject

            return textView
        }

    }
}