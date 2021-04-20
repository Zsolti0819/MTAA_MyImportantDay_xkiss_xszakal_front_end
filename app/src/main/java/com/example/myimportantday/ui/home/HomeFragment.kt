package com.example.myimportantday.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
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
                        val event = arrayOfNulls<String>(eventList?.events!!.size)

                        for (i: Int in eventList.events.indices) {
                            event[i] = eventList.events[i].subject
                        }


                        val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_dropdown_item_1line, event)

                        val listView = root.findViewById<ListView>(R.id.listView)
                        listView.adapter = adapter



                        Log.d("R.BODY", eventList.toString())

//                        val listView = root.findViewById<ListView>(R.id.listView)
//                        listView.adapter =
//                            response.body()?.let { it1 -> MyCustomAdapter(it1, context!!) }

                    }
                })
        }

        return root
    }


}