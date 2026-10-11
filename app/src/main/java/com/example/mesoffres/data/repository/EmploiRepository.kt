package com.example.mesoffres.data.repository


import android.content.Context
import androidx.room.Room
import com.example.mesoffres.data.local.AppDatabase
import com.example.mesoffres.data.local.MIGRATION_1_2
import com.example.mesoffres.data.model.JobApplication
import com.example.mesoffres.data.model.LetterModel
import com.example.mesoffres.data.model.Offer
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock




/**
 * Repository unique de l'application.
 *
 * Son rôle est de cacher Room au ViewModel.
 * Plus tard, on pourra ajouter une API distante ici sans modifier l'UI.
 */
class EmploiRepository(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "emploi_stages.db"
    ).addMigrations(MIGRATION_1_2).build()

    private val appContext = context.applicationContext

    private val offerDao = database.offerDao()
    private val applicationDao = database.applicationDao()
    private val letterDao = database.letterDao()
    // Le verrou évite que deux appuis rapprochés créent deux lignes pour la même offre.
    private val applicationInsertMutex = Mutex()

    fun observeOffers(): Flow<List<Offer>> = offerDao.observeOffers()

    fun observeApplications(): Flow<List<JobApplication>> =
        applicationDao.observeApplications()

    fun observeLetters(): Flow<List<LetterModel>> =
        letterDao.observeLetters()

    suspend fun getOffer(id: Long): Offer? =
        offerDao.getOfferById(id)

    // Enregistrer une offre ajoutée manuellement.
    suspend fun addOffer(offer: Offer): Long {
        return offerDao.insertOffer(offer)
    }

    suspend fun toggleFavorite(offer: Offer) {
        offerDao.update(
            offer.copy(isFavorite = !offer.isFavorite)
        )
    }

    suspend fun applyToOffer(offer: Offer): Boolean = applicationInsertMutex.withLock {
        // Vérifier dans Room empêche une nouvelle candidature si l'utilisateur revient sur le formulaire.
        if (applicationDao.findForOffer(offer.id) != null) return@withLock false

        applicationDao.insert(
            JobApplication(
                offerId = offer.id,
                offerTitle = offer.title,
                company = offer.company,
                city = offer.city,
                status = "À envoyer",
                // Aucune date d'envoi n'existe encore : l'utilisateur n'a pas contacté l'entreprise.
                dateApplied = "",
                deadline = offer.deadline
            )
        )
        true
    }

    suspend fun updateStatus(id: Long, status: String, followUpDate: String = "") {
        // Cette date ne sera copiée dans Room que si le statut choisi est « Envoyée ».
        val today = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH)
        )
        applicationDao.updateStatus(id, status, today, followUpDate)
    }

    /** Enregistre dans Room le texte modifié par l'utilisateur. */
    suspend fun updateLetter(letter: LetterModel) {
        letterDao.update(letter)
    }


    suspend fun seedDatabase() {
        val preferences = appContext.getSharedPreferences(
            "mesoffres_preferences",
            Context.MODE_PRIVATE
        )

        val seedCompleted = preferences.getBoolean(
            "demo_data_seeded",
            false
        )

        if (seedCompleted) return

        // Si des offres existent déjà, on évite de réinsérer les données de démonstration.
        if (offerDao.getOfferById(1L) != null) {
            preferences.edit()
                .putBoolean("demo_data_seeded", true)
                .apply()
            return
        }

        val offers = listOf(
            Offer(
                id = 1,
                title = "Développeur Mobile (Kotlin)",
                company = "Orange Guinée",
                city = "Conakry",
                type = "Stage",
                sector = "Mobile",
                deadline = "25 sept. 2026",
                description = "Orange Guinée recherche un développeur mobile en Kotlin pour renforcer son équipe digitale et participer au développement d'applications Android.",
                contact = "recrutement@orange-guinee.com"
            ),
            Offer(
                id = 2,
                title = "Développeur Fullstack",
                company = "MTN Guinée",
                city = "Conakry",
                type = "Emploi",
                sector = "Web",
                deadline = "29 sept. 2026",
                description = "Développement d'applications web modernes avec React, Node.js et bases de données.",
                contact = "jobs@mtn.com"
            ),
            Offer(
                id = 3,
                title = "Assistant Chef de Projet",
                company = "UNICEF",
                city = "Conakry",
                type = "Stage",
                sector = "Gestion de projet",
                deadline = "28 sept. 2026",
                description = "Participation au suivi des projets, à la préparation des réunions et au reporting.",
                contact = "jobs@unicef.org"
            ),
            Offer(
                id = 4,
                title = "Développeur Web",
                company = "Guinée Games",
                city = "Conakry",
                type = "Emploi",
                sector = "Web",
                deadline = "5 oct. 2026",
                description = "Conception et maintenance de fonctionnalités web pour les plateformes de l'entreprise.",
                contact = "rh@guineegames.com"
            )
        )

        offerDao.insertAll(offers)

        letterDao.insertAll(
            listOf(
                LetterModel(
                    title = "Candidature pour un stage",
                    content = "Madame, Monsieur,\n\nActuellement étudiant en informatique, je souhaite mettre mes compétences au service de votre équipe..."
                ),
                LetterModel(
                    title = "Candidature spontanée",
                    content = "Madame, Monsieur,\n\nJe me permets de vous adresser ma candidature spontanée..."
                )
            )
        )

        // On mémorise que l'initialisation a été effectuée.
        preferences.edit()
            .putBoolean("demo_data_seeded", true)
            .apply()
    }



    suspend fun deleteOffer(offerId: Long) {
        applicationDao.deleteApplicationsForOffer(offerId)
        offerDao.deleteOfferById(offerId)
    }

    suspend fun deleteAllOffers() {
        applicationDao.deleteAllApplications()
        offerDao.deleteAll()
    }





}
