package com.example.mesoffres.notifications



import android.content.Context
import androidx.work.WorkManager
import com.example.mesoffres.data.model.Offer
import com.example.mesoffres.utils.OfferDateUtils
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object OfferReminderManager {

    fun scheduleOfferDeadlineReminder(
        context: Context,
        offer: Offer
    ) {
        val deadline = OfferDateUtils.parseDate(offer.deadline)

        if (deadline == null) {
            cancelOfferDeadlineReminder(context, offer.id)
            return
        }

        val today = LocalDate.now()

        // Pas de rappel pour une offre dont la date limite est passée.
        if (deadline.isBefore(today)) {
            cancelOfferDeadlineReminder(context, offer.id)
            return
        }

        // Premier rappel : 3 jours avant la date limite.
        val reminderDate = deadline.minusDays(3)

        // Si le rappel est déjà passé, utiliser aujourd'hui.
        val effectiveDate = if (reminderDate.isBefore(today)) {
            today
        } else {
            reminderDate
        }


        val delayMinutes = ChronoUnit.MINUTES.between(
            java.time.LocalDateTime.now(),
            effectiveDate.atTime(9, 0)
        ).coerceAtLeast(1L)

        ReminderScheduler.scheduleReminder(
            context = context,
            reminderId = "offer_deadline_${offer.id}",
            title = "Rappel de date limite",
            message = "L'offre ${offer.title} chez ${offer.company} expire le ${offer.deadline}.",
            delayMinutes = delayMinutes
        )

    }

    fun cancelOfferDeadlineReminder(
        context: Context,
        offerId: Long
    ) {
        ReminderScheduler.cancelReminder(
            context,
            "offer_deadline_$offerId"
        )
    }

    fun scheduleAllOfferReminders(
        context: Context,
        offers: List<Offer>
    ) {
        offers.forEach { offer ->
            scheduleOfferDeadlineReminder(context, offer)
        }
    }
}
