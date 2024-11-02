package com.kuroakevizago.dicodingapp.ui.detail

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.dicodingapp.data.remote.response.Event
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.data.repository.DicodingEventRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel : ViewModel() {

    // Mutable Live Data to hold the API Data
    private val _eventData = MutableLiveData<Event?>()
    val eventData: LiveData<Event?> = _eventData

    private val _loadStateDetail = MutableLiveData<ViewState>()
    val loadStateDetail: LiveData<ViewState> = _loadStateDetail

    fun fetchEventData(id: Int) {
        _loadStateDetail.value = ViewState.Loading

        if (_eventData.value?.id == id) {
            _loadStateDetail.value = ViewState.Loaded
            return
        }

        viewModelScope.launch {
            try {
                val eventResponse = async {
                    DicodingEventRepository.getEventById(id)
                }.await()

                if (eventResponse.isSuccessful) {
                    _loadStateDetail.value = ViewState.Loaded
                    _eventData.value = eventResponse.body()?.event
                } else {
                    _loadStateDetail.value = ViewState.Failed
                    Log.e(ContentValues.TAG, "onFailure: ${eventResponse.message()}")
                }
            } catch (e: HttpException) {
                _loadStateDetail.value = ViewState.Failed
                Log.e(ContentValues.TAG, "onFailure: ${e.message.toString()}")
            } catch (e: Throwable) {
                _loadStateDetail.value = ViewState.Failed
                Log.e(ContentValues.TAG, "onFailure: ${e.message.toString()}")
            }
        }
    }
}