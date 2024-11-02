package com.kuroakevizago.dicodingapp.data.remote.api

import com.kuroakevizago.dicodingapp.data.remote.response.DicodingEventListResponse
import com.kuroakevizago.dicodingapp.data.remote.response.DicodingEventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getActiveEvents(): Response<DicodingEventListResponse>

    @GET("events?active=0")
    suspend fun getFinishedEvents(): Response<DicodingEventListResponse>

    @GET("events?active=-1")
    suspend fun getSearchEvents(@Query("q") keyword: String): Response<DicodingEventListResponse>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): Response<DicodingEventResponse>

    @GET("events?active=-1&limit1")
    suspend fun getEventNotification(): Response<DicodingEventListResponse>
}