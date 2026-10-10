package com.example.mesoffres.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Type d'offre.
 */
enum class OfferType {
    EMPLOI,
    STAGE
}

/**
 * Statut d'une candidature.
 */
enum class ApplicationStatus {
    A_ENVOYER,
    ENVOYEE,
    ENTRETIEN,
    ACCEPTEE,
    REFUSEE
}

/**
 * Entité Room représentant une offre.
 *
 * Toutes les informations nécessaires à l'écran de détail
 * sont stockées localement.
 */
@Entity(tableName = "offers")
data class Offer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val company: String,
    val city: String,
    val type: String,
    val sector: String,
    val deadline: String,
    val description: String,
    val contact: String,
    val isFavorite: Boolean = false
)

/**
 * Entité Room représentant une candidature.
 */
@Entity(tableName = "applications")
data class JobApplication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val offerId: Long,
    val offerTitle: String,
    val company: String,
    val city: String,
    val status: String,
    // Vide tant que la candidature est « À envoyer », puis rempli au passage à « Envoyée ».
    val dateApplied: String,
    val deadline: String,
    val notes: String = "",
    // Date choisie pour relancer l'entreprise après l'envoi de la candidature.
    val followUpDate: String = ""
)

/**
 * Entité Room pour les modèles de lettres.
 */
@Entity(tableName = "letter_models")
data class LetterModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String
)
