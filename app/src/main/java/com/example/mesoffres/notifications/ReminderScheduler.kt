package com.example.mesoffres.notifications


import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        reminderId: String,
        title: String,
        message: String,
        delayMinutes: Long
    ) {
        val data = Data.Builder()
            .putString("title", title)
            .putString("message", message)
            .putInt("notificationId", reminderId.hashCode())
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(
                delayMinutes.coerceAtLeast(1L),
                TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            reminderId,
            androidx.work.ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder(context: Context, reminderId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(reminderId)
    }


    fun scheduleTestReminder(context: Context) {
        scheduleReminder(
            context = context,
            reminderId = "test_notification_mesoffres",
            title = "Test MesOffres",
            message = "Bravo ! Les notifications de MesOffres fonctionnent.",
            delayMinutes = 1L
        )
    }

}
