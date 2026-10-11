package com.example.mesoffres.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mesoffres.ui.screens.ApplicationsScreen
import com.example.mesoffres.ui.screens.ApplyScreen
import com.example.mesoffres.ui.screens.FavoriteOffersScreen
import com.example.mesoffres.ui.screens.HomeScreen
import com.example.mesoffres.ui.screens.LetterDetailScreen
import com.example.mesoffres.ui.screens.LettersScreen
import com.example.mesoffres.ui.screens.OfferDetailScreen
import com.example.mesoffres.ui.screens.OffersScreen
import com.example.mesoffres.ui.screens.ProfileScreen
import com.example.mesoffres.ui.screens.RemindersScreen
import com.example.mesoffres.ui.viewmodel.EmploiViewModel
import com.example.mesoffres.ui.screens.AddOfferScreen

/**
 * Graphe de navigation complet.
 */
@Composable
fun AppNavigation(viewModel: EmploiViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // Le MVP s'ouvre directement sur l'écran Offres, premier écran de la maquette.
        startDestination = Routes.OFFERS
    ) {

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onOpenOffers = { navController.navigate(Routes.OFFERS) },
                onOpenApplications = { navController.navigate(Routes.APPLICATIONS) },
                onOpenReminders = { navController.navigate(Routes.REMINDERS) },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.OFFERS) {
            OffersScreen(
                viewModel = viewModel,
                onOpenOffer = { id ->
                    navController.navigate(Routes.offerDetail(id))
                },
                onOpenApplications = { navController.navigate(Routes.APPLICATIONS) },
                onOpenDashboard = { navController.navigate(Routes.HOME) },
                onOpenAddOffer = {
                    navController.navigate(Routes.ADD_OFFER)
                }
            )
        }


        composable(Routes.ADD_OFFER) {
            AddOfferScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }


        composable(
            route = Routes.OFFER_DETAIL,
            arguments = listOf(
                navArgument("offerId") { type = NavType.LongType }
            )
        ) { entry ->

            val id = entry.arguments?.getLong("offerId") ?: return@composable

            OfferDetailScreen(
                offerId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onApply = { navController.navigate(Routes.apply(id)) }
            )
        }

        composable(
            route = Routes.APPLY,
            arguments = listOf(
                navArgument("offerId") { type = NavType.LongType }
            )
        ) { entry ->

            val id = entry.arguments?.getLong("offerId") ?: return@composable

            ApplyScreen(
                offerId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSubmitted = {
                    navController.navigate(Routes.APPLICATIONS) {
                        // Après l'enregistrement, on revient à la racine des écrans principaux.
                        popUpTo(Routes.OFFERS)
                    }
                }
            )
        }

        composable(Routes.APPLICATIONS) {
            ApplicationsScreen(
                viewModel = viewModel,
                onOpenOffers = { navController.navigate(Routes.OFFERS) { launchSingleTop = true } },
                onOpenDashboard = { navController.navigate(Routes.HOME) { launchSingleTop = true } }
            )
        }

        composable(Routes.LETTERS) {
            LettersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenLetter = { id ->
                    navController.navigate(Routes.letterDetail(id))
                }
            )
        }

        composable(
            Routes.LETTER_DETAIL,
            arguments = listOf(
                navArgument("letterId") { type = NavType.LongType }
            )
        ) { entry ->

            val id = entry.arguments?.getLong("letterId") ?: return@composable

            LetterDetailScreen(
                letterId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.REMINDERS) {
            RemindersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenLetters = { navController.navigate(Routes.LETTERS) },
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) }
            )
        }

        composable(Routes.FAVORITES) {
            FavoriteOffersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenOffers = { navController.navigate(Routes.OFFERS) { launchSingleTop = true } },
                onOpenOffer = { id -> navController.navigate(Routes.offerDetail(id)) }
            )
        }
    }
}
