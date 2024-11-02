package com.kuroakevizago.dicodingapp.ui.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.dicodingapp.data.remote.response.ListEventsItem
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.data.repository.DicodingEventRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel : ViewModel() {

    // Mutable Live Data to hold the API Data
    private val _upcomingEvents = MutableLiveData<List<ListEventsItem?>?>()
    val upcomingEvents : LiveData<List<ListEventsItem?>?> = _upcomingEvents

    private val _loadStateUpcomingEvents = MutableLiveData<ViewState>()
    val loadStateUpcomingEvents: LiveData<ViewState> = _loadStateUpcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem?>?>()
    val finishedEvents : LiveData<List<ListEventsItem?>?> = _finishedEvents

    private val _loadStateFinishedEvents = MutableLiveData<ViewState>()
    val loadStateFinishedEvents: LiveData<ViewState> = _loadStateFinishedEvents

    private val _searchEvents = MutableLiveData<List<ListEventsItem?>?>()
    val searchEvents : LiveData<List<ListEventsItem?>?> = _searchEvents

    private val _loadStateSearchEvents = MutableLiveData<ViewState>()
    val loadStateSearchEvents: LiveData<ViewState> = _loadStateSearchEvents

    init {
        fetchUpcomingEvents()
        fetchFinishedEvents()
    }

    fun fetchUpcomingEvents() {
        _loadStateUpcomingEvents.value = ViewState.Loading

        if (!_upcomingEvents.value.isNullOrEmpty()) {
            _loadStateUpcomingEvents.value = ViewState.Loaded
            return
        }

        viewModelScope.launch {
            try {
                val eventResponse = async {
                    DicodingEventRepository.getActiveEvents()
                }.await()

                if (eventResponse.isSuccessful) {
                    _loadStateUpcomingEvents.value = ViewState.Loaded
                    _upcomingEvents.value = eventResponse.body()?.listEvents
                } else {
                    _loadStateUpcomingEvents.value = ViewState.Failed
                    Log.e(TAG, "onFailure: ${eventResponse.message()}")
                }
            } catch (e: HttpException) {
                _loadStateUpcomingEvents.value = ViewState.Failed
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            } catch (e: Throwable) {
                _loadStateUpcomingEvents.value = ViewState.Failed
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            }
        }
    }

    fun fetchFinishedEvents() {
        _loadStateFinishedEvents.value = ViewState.Loading

        if (!_finishedEvents.value.isNullOrEmpty()) {
            _loadStateFinishedEvents.value = ViewState.Loaded
            return
        }

        viewModelScope.launch {
            try {
                val eventResponse = async {
                    DicodingEventRepository.getFinishedEvents()
                }.await()

                if (eventResponse.isSuccessful) {
                    _loadStateFinishedEvents.value = ViewState.Loaded
                    _finishedEvents.value = eventResponse.body()?.listEvents
                } else {
                    _loadStateFinishedEvents.value = ViewState.Failed
                    Log.e(TAG, "onFailure: ${eventResponse.message()}")
                }
            } catch (e: HttpException) {
                _loadStateFinishedEvents.value = ViewState.Failed
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            } catch (e: Throwable) {
                _loadStateFinishedEvents.value = ViewState.Failed
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            }
        }
    }

    fun fetchSearchEvents(keyword: String) {
        _loadStateSearchEvents.value = ViewState.Loading

        if (!_searchEvents.value.isNullOrEmpty()) {
            _loadStateSearchEvents.value = ViewState.Loaded
            return
        }

        viewModelScope.launch {
            try {
                val eventResponse = async {
                    DicodingEventRepository.getSearchedEvents(keyword)
                }.await()

                if (eventResponse.isSuccessful) {
                    _loadStateSearchEvents.value = ViewState.Loaded
                    _searchEvents.value = eventResponse.body()?.listEvents
                } else {
                    _loadStateSearchEvents.value = ViewState.Failed
                    Log.e(TAG, "onFailure: ${eventResponse.message()}")
                }
            } catch (e: HttpException) {
                _loadStateSearchEvents.value = ViewState.Failed
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            } catch (e: Throwable) {
                _loadStateSearchEvents.value = ViewState.Failed
                Log.e(TAG, "onFailure: ${e.message.toString()}")
            }
        }
    }

}