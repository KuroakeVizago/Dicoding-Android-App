package com.kuroakevizago.dicodingapp.data.repository

import com.kuroakevizago.dicodingapp.data.remote.api.Api
import com.kuroakevizago.dicodingapp.data.remote.response.DicodingEventListResponse
import com.kuroakevizago.dicodingapp.data.remote.response.DicodingEventResponse
import retrofit2.Response

object DicodingEventRepository {
    suspend fun getActiveEvents(): Response<DicodingEventListResponse> = Api.getApiService().getActiveEvents()

    suspend fun getFinishedEvents(): Response<DicodingEventListResponse> = Api.getApiService().getFinishedEvents()

    suspend fun getSearchedEvents(keyword: String): Response<DicodingEventListResponse> = Api.getApiService().getSearchEvents(keyword)

    suspend fun getEventById(id: Int): Response<DicodingEventResponse> = Api.getApiService().getEventById(id)

    suspend fun getEventNotification(): Response<DicodingEventListResponse> = Api.getApiService().getEventNotification()
}