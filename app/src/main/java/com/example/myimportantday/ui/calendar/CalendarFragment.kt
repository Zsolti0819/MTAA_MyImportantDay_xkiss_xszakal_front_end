package com.example.myimportantday.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myimportantday.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CalendarFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)

        val calendar = root.findViewById<CalendarView>(R.id.calendarView)
        calendar.setOnDateChangeListener { myCalendar, year, month, dayOfMonth ->  Log.d("Selected date:", "$year/${month+1}/$dayOfMonth")}

        return root
    }
}