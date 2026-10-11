package com.example.mesoffres.notifications


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title")
            ?: "Rappel MesOffres"

        val message = inputData.getString("message")
            ?: "Tu as un rappel à consulter."

        val notificationId = inputData.getInt(
            "notificationId",
            System.currentTimeMillis().toInt()
        )

        NotificationHelper.createNotificationChannel(applicationContext)

        NotificationHelper.showNotification(
            context = applicationContext,
            notificationId = notificationId,
            title = title,
            message = message
        )

        return Result.success()
    }
}
