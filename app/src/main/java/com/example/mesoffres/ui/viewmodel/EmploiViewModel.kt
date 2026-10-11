package com.example.mesoffres.ui.viewmodel

import com.example.mesoffres.notifications.ApplicationReminderManager
import com.example.mesoffres.notifications.OfferReminderManager
import kotlinx.coroutines.flow.collect
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mesoffres.data.model.JobApplication
import com.example.mesoffres.data.model.LetterModel
import com.example.mesoffres.data.model.Offer
import com.example.mesoffres.data.repository.EmploiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * ViewModel principal.
 *
 * Il contient l'état nécessaire aux écrans et communique avec le Repository.
 */
class EmploiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmploiRepository(application)

    // La recherche reste dans le ViewModel pour être réutilisable depuis le tableau de bord.
    private val _offerSearchQuery = MutableStateFlow("")
    val offerSearchQuery = _offerSearchQuery.asStateFlow()

    /**
     * Liste locale des offres.
     */
    val offers: StateFlow<List<Offer>> =
        repository.observeOffers()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    /**
     * Liste locale des candidatures.
     */
    val applications: StateFlow<List<JobApplication>> =
        repository.observeApplications()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    /**
     * Liste des modèles de lettres.
     */
    val letters: StateFlow<List<LetterModel>> =
        repository.observeLetters()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    init {
        viewModelScope.launch {
            // Préparer les données de démonstration si nécessaire.
            repository.seedDatabase()

            // Observer les offres enregistrées dans Room.
            offers.collect { currentOffers ->
                OfferReminderManager.scheduleAllOfferReminders(
                    context = getApplication<Application>(),
                    offers = currentOffers
                )
            }
        }
    }


    /**
     * Ajoute ou retire une offre des favoris.
     */
    fun toggleFavorite(offer: Offer) {
        viewModelScope.launch {
            repository.toggleFavorite(offer)
        }
    }


    /**
     * Supprime une offre et annule son rappel de date limite.
     */

    fun deleteOffer(offer: Offer, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            val relatedApplications = applications.value.filter {
                it.offerId == offer.id
            }

            repository.deleteOffer(offer.id)

            OfferReminderManager.cancelOfferDeadlineReminder(
                context = getApplication<Application>(),
                offerId = offer.id
            )

            relatedApplications.forEach { application ->
                ApplicationReminderManager.cancelFollowUpReminder(
                    context = getApplication<Application>(),
                    applicationId = application.id
                )
            }

            onDeleted()
        }
    }



    /**
     * Supprime toutes les offres et annule leurs rappels.
     */

    fun deleteAllOffers(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            val currentOffers = offers.value
            val currentApplications = applications.value

            repository.deleteAllOffers()

            val context = getApplication<Application>()

            currentOffers.forEach { offer ->
                OfferReminderManager.cancelOfferDeadlineReminder(
                    context = context,
                    offerId = offer.id
                )
            }

            currentApplications.forEach { application ->
                ApplicationReminderManager.cancelFollowUpReminder(
                    context = context,
                    applicationId = application.id
                )
            }

            onDeleted()
        }
    }





    /**
     * Ajoute une offre saisie manuellement.
     * onSaved reçoit l'identifiant de la nouvelle offre.
     */

    fun addOffer(
        offer: Offer,
        onSaved: (Long) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val offerId = repository.addOffer(offer)

                // Programmer immédiatement le rappel de date limite.
                val savedOffer = offer.copy(id = offerId)

                OfferReminderManager.scheduleOfferDeadlineReminder(
                    context = getApplication<Application>(),
                    offer = savedOffer
                )



                onSaved(offerId)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }


    /**
     * Modifie une offre existante et actualise son rappel.
     */
    fun updateOffer(
        offer: Offer,
        onSaved: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Mettre à jour l'offre existante dans Room.
                repository.updateOffer(offer)

                // Actualiser le rappel lié à sa date limite.
                OfferReminderManager.scheduleOfferDeadlineReminder(
                    context = getApplication<Application>(),
                    offer = offer
                )

                onSaved()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }




    /** Met à jour le texte utilisé par la liste des offres. */
    fun updateOfferSearchQuery(query: String) {
        _offerSearchQuery.value = query
    }

    /**
     * Crée une candidature locale.
     */
    fun apply(offer: Offer, onSaved: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val wasCreated = repository.applyToOffer(offer)
            // Le booléen indique à l'écran si la candidature est nouvelle ou existait déjà.
            onSaved(wasCreated)
        }
    }

    /**
     * Modifie le statut d'une candidature.
     */

    fun updateApplicationStatus(
        id: Long,
        status: String,
        followUpDate: String = ""
    ) {
        viewModelScope.launch {
            // Enregistrer le statut et la date de relance dans Room.
            repository.updateStatus(id, status, followUpDate)

            // Récupérer la candidature mise à jour.
            val updatedApplication = repository
                .observeApplications()
                .first()
                .find { it.id == id }

            // Programmer ou annuler le rappel correspondant.
            if (updatedApplication != null) {
                ApplicationReminderManager.scheduleFollowUpReminder(
                    context = getApplication<Application>(),
                    application = updatedApplication
                )
            }
        }
    }

    /** Demande au Repository de sauvegarder le modèle avant de confirmer à l'interface. */
    fun updateLetter(letter: LetterModel, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateLetter(letter)
            onSaved()
        }
    }
}
