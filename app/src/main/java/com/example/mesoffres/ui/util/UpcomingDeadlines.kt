package com.example.mesoffres.ui.util

import com.example.mesoffres.data.model.JobApplication
import com.example.mesoffres.data.model.Offer
import java.time.LocalDate
import java.util.Locale

/** Échéance d'offre ou de candidature, mise sous une forme commune pour les écrans. */
data class UpcomingDeadline(
    val title: String,
    val company: String,
    val dateLabel: String,
    val date: LocalDate,
    val applicationStatus: String? = null,
    // Permet de distinguer une date limite d'offre d'une relance personnelle.
    val kind: String = "Date limite"
)

/**
 * Construit la liste des dates limites futures.
 * Une candidature active remplace l'offre correspondante pour éviter les doublons.
 */
fun buildUpcomingDeadlines(
    offers: List<Offer>,
    applications: List<JobApplication>,
    limit: Int = 5,
    today: LocalDate = LocalDate.now()
): List<UpcomingDeadline> {
    val activeApplications = applications.filter {
        it.status == "À envoyer" || it.status == "Envoyée" || it.status == "Entretien"
    }
    val followedOfferIds = applications.map { it.offerId }.toSet()

    val applicationDeadlines = activeApplications.mapNotNull { application ->
        val date = parseFrenchDeadline(application.deadline) ?: return@mapNotNull null
        if (date.isBefore(today)) return@mapNotNull null
        UpcomingDeadline(
            title = application.offerTitle,
            company = application.company,
            dateLabel = application.deadline,
            date = date,
            applicationStatus = application.status
        )
    }

    // Une relance est une échéance séparée de la date limite de l'offre.
    val followUpDeadlines = applications
        .filter { it.status == "Envoyée" && it.followUpDate.isNotBlank() }
        .mapNotNull { application ->
            val date = parseFrenchDeadline(application.followUpDate) ?: return@mapNotNull null
            if (date.isBefore(today)) return@mapNotNull null
            UpcomingDeadline(
                title = "Relancer ${application.offerTitle}",
                company = application.company,
                dateLabel = application.followUpDate,
                date = date,
                applicationStatus = application.status,
                kind = "Relance"
            )
        }

    // Les offres sans candidature restent visibles, pour aider à ne pas oublier leur date limite.
    val offerDeadlines = offers
        .filter { it.id !in followedOfferIds }
        .mapNotNull { offer ->
            val date = parseFrenchDeadline(offer.deadline) ?: return@mapNotNull null
            if (date.isBefore(today)) return@mapNotNull null
            UpcomingDeadline(offer.title, offer.company, offer.deadline, date)
        }

    return (applicationDeadlines + followUpDeadlines + offerDeadlines)
        .distinctBy { Triple(it.title, it.date, it.kind) }
        .sortedBy { it.date }
        .take(limit)
}

/** Lit les dates françaises conservées sous forme de texte dans la base locale. */
fun parseFrenchDeadline(value: String): LocalDate? {
    val parts = value.trim().lowercase(Locale.FRENCH).replace(".", "").split(Regex("\\s+"))
    if (parts.size != 3) return null

    val monthNumber = when (parts[1]) {
        "janv", "janvier" -> 1
        "févr", "fevr", "février", "fevrier" -> 2
        "mars" -> 3
        "avr", "avril" -> 4
        "mai" -> 5
        "juin" -> 6
        "juil", "juillet" -> 7
        "août", "aout" -> 8
        "sept", "septembre" -> 9
        "oct", "octobre" -> 10
        "nov", "novembre" -> 11
        "déc", "dec", "décembre", "decembre" -> 12
        else -> return null
    }

    // Une date mal formée est ignorée au lieu de faire planter l'écran.
    return runCatching {
        LocalDate.of(parts[2].toInt(), monthNumber, parts[0].toInt())
    }.getOrNull()
}
