package com.example.mesoffres.notifications

import android.content.Context
import com.example.mesoffres.data.model.JobApplication
import com.example.mesoffres.utils.OfferDateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Gestion des rappels de relance des candidatures.
 */
object ApplicationReminderManager {

    fun scheduleFollowUpReminder(
        context: Context,
        application: JobApplication
    ) {
        val reminderId = "application_follow_up_${application.id}"

        // Une date de relance est nécessaire.
        val followUpDate = OfferDateUtils.parseDate(
            application.followUpDate
        )

        // Sans date valide, ou si la date est passée, annuler le rappel.
        if (
            followUpDate == null ||
            followUpDate.isBefore(LocalDate.now())
        ) {
            ReminderScheduler.cancelReminder(context, reminderId)
            return
        }

        // Programmer la notification à 9 h le jour de la relance.
        val reminderDateTime = followUpDate.atTime(9, 0)
        val now = LocalDateTime.now()

        val delayMinutes = ChronoUnit.MINUTES.between(
            now,
            reminderDateTime
        ).coerceAtLeast(1L)

        ReminderScheduler.scheduleReminder(
            context = context,
            reminderId = reminderId,
            title = "Relance de candidature",
            message = "Pense à relancer ta candidature chez ${application.company} pour le poste « ${application.offerTitle} ».",
            delayMinutes = delayMinutes
        )
    }

    fun cancelFollowUpReminder(
        context: Context,
        applicationId: Long
    ) {
        val reminderId = "application_follow_up_$applicationId"
        ReminderScheduler.cancelReminder(context, reminderId)
    }
}
