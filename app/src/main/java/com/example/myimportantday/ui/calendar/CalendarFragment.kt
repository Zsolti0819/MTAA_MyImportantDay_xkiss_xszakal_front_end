package com.example.myimportantday.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myimportantday.R

class CalendarFragment : Fragment() {

    private lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        calendarViewModel =
                ViewModelProvider(this).get(CalendarViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        return root
    }
}