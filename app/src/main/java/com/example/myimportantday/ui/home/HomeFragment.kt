package com.example.myimportantday.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myimportantday.R
import com.example.myimportantday.repository.Repository

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val repository = Repository()
        val viewModelFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        homeViewModel.getAllEvents()
        homeViewModel.myResponse.observe(viewLifecycleOwner, Observer { response ->
            root.findViewById<TextView>(R.id.textView).text = response.toString()

        })
        return root
    }
}