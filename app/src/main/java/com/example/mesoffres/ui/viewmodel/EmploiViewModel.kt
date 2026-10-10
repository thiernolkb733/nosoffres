package com.example.mesoffres.ui.viewmodel

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
        // Au premier lancement, on crée les données de démonstration.
        viewModelScope.launch {
            repository.seedDatabase()
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
    fun updateApplicationStatus(id: Long, status: String, followUpDate: String = "") {
        viewModelScope.launch {
            repository.updateStatus(id, status, followUpDate)
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
