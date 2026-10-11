package com.example.mesoffres.ui.navigation

/**
 * Toutes les routes de navigation.
 *
 * Garder les routes dans un seul fichier évite les fautes de frappe.
 */
object Routes {
    const val HOME = "home"
    const val OFFERS = "offers"

    const val ADD_OFFER = "add_offer"

    const val EDIT_OFFER = "edit_offer/{offerId}"
    const val OFFER_DETAIL = "offer_detail/{offerId}"
    const val APPLICATIONS = "applications"
    const val APPLY = "apply/{offerId}"
    const val LETTERS = "letters"
    const val LETTER_DETAIL = "letter_detail/{letterId}"
    const val REMINDERS = "reminders"
    const val PROFILE = "profile"
    const val FAVORITES = "favorites"

    fun offerDetail(id: Long) = "offer_detail/$id"

    fun editOffer(id: Long) = "edit_offer/$id"
    fun apply(id: Long) = "apply/$id"
    fun letterDetail(id: Long) = "letter_detail/$id"
}
