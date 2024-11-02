package com.kuroakevizago.dicodingapp.worker

import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kuroakevizago.dicodingapp.R
import com.kuroakevizago.dicodingapp.data.remote.response.ListEventsItem
import com.kuroakevizago.dicodingapp.data.repository.DicodingEventRepository

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        var event: ListEventsItem? = null

        try {
            val response = DicodingEventRepository.getEventNotification()
            if (response.isSuccessful) {
                event = response.body()?.listEvents?.first()
            } else {
                Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        event?.let {
            showNotification(it)
        }
        return Result.success()
    }

    private fun showNotification(event: ListEventsItem) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "dailyReminderChannel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Upcoming Event")
            .setContentText("Event ${event.name} start at ${event.beginTime}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}