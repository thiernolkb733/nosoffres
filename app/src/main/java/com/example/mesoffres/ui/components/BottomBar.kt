package com.example.mesoffres.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Navigation inférieure commune aux écrans principaux.
 */
@Composable
fun MainBottomBar(
    current: String,
    onHome: () -> Unit,
    onOffers: () -> Unit,
    onApplications: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = current == "home",
            onClick = onHome,
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Accueil")
            },
            label = { Text("Accueil") }
        )

        NavigationBarItem(
            selected = current == "offers",
            onClick = onOffers,
            icon = {
                Icon(Icons.Default.BusinessCenter, contentDescription = "Offres")
            },
            label = { Text("Offres") }
        )

        NavigationBarItem(
            selected = current == "applications",
            onClick = onApplications,
            icon = {
                Icon(Icons.Default.Description, contentDescription = "Candidatures")
            },
            label = { Text("Candidatures") }
        )

        NavigationBarItem(
            selected = current == "profile",
            onClick = onProfile,
            icon = {
                Icon(Icons.Default.Person, contentDescription = "Profil")
            },
            label = { Text("Profil") }
        )
    }
}
