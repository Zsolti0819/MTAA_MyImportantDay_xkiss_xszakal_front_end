package com.example.myimportantday.ui.newEvent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myimportantday.R

class NewEventFragment : Fragment() {

    private lateinit var newEventViewModel: NewEventViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        newEventViewModel =
                ViewModelProvider(this).get(NewEventViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_new_event, container, false)
        return root
    }
}

