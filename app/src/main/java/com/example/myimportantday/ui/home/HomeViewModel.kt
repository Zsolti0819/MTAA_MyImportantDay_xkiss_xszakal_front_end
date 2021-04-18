package com.example.myimportantday.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myimportantday.model.Event
import com.example.myimportantday.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    val myResponse: MutableLiveData<Event> = MutableLiveData()

    fun getAllEvents() {
        viewModelScope.launch {
            val response = repository.getEvents()
            myResponse.value = response
        }
    }
}