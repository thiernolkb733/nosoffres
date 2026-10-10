package com.example.mesoffres.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val ProfileBackground = Color(0xFF191A18)
private val ProfileSurface = Color(0xFF20211F)
private val ProfileOrange = Color(0xFFE86A3A)
private val ProfileMuted = Color(0xFFAAAAA6)

/** Page de profil simplifiée : elle n'affiche pas de données personnelles fictives. */
@Composable
fun ProfileScreen(
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onOpenLetters: () -> Unit,
    onOpenFavorites: () -> Unit
) {
    // Le nombre de favoris vient du même flux Room que celui utilisé par l'écran Offres.
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val favoriteCount = offers.count { it.isFavorite }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfileOrange)
                .padding(end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Mon profil",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ProfileSurface)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            text = "Informations personnelles",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Aucune information personnelle n'est enregistrée dans cette version.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ProfileMuted,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Mes outils de candidature",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenLetters),
                    colors = CardDefaults.cardColors(containerColor = ProfileSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = ProfileOrange)
                        Column(modifier = Modifier.padding(start = 14.dp)) {
                            Text(
                                text = "Mes modèles de lettres",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Consulter, modifier et partager vos modèles.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfileMuted
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenFavorites),
                    colors = CardDefaults.cardColors(containerColor = ProfileSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = ProfileOrange)
                        Column(modifier = Modifier.padding(start = 14.dp)) {
                            Text(
                                text = "Offres favorites",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = if (favoriteCount == 0) {
                                    "Aucun favori. Touchez l'étoile sur une offre pour l'enregistrer."
                                } else {
                                    "$favoriteCount offre${if (favoriteCount > 1) "s" else ""} enregistrée${if (favoriteCount > 1) "s" else ""}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfileMuted
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ProfileSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = ProfileOrange)
                        Column(modifier = Modifier.padding(start = 14.dp)) {
                            Text(
                                text = "Données sur cet appareil",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Les offres, candidatures et lettres restent accessibles hors ligne.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfileMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
