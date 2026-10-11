package com.example.mesoffres.ui.screens


import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.mesoffres.ui.components.OfferListCard
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val FavoritesBackground = Color(0xFF191A18)
private val FavoritesOrange = Color(0xFFE86A3A)
private val FavoritesMuted = Color(0xFFAAAAA6)

/** Liste locale des offres marquées avec l'étoile depuis l'écran Offres. */
@Composable
fun FavoriteOffersScreen(
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onOpenOffers: () -> Unit,
    onOpenOffer: (Long) -> Unit
) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val favorites = offers.filter { it.isFavorite }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FavoritesBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FavoritesOrange)
                .statusBarsPadding()
                .padding(end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Mes offres favorites",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = FavoritesOrange
                )
                Text(
                    text = "Aucune offre favorite pour le moment.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FavoritesMuted,
                    modifier = Modifier.padding(top = 12.dp, bottom = 18.dp)
                )
                Button(
                    onClick = onOpenOffers,
                    colors = ButtonDefaults.buttonColors(containerColor = FavoritesOrange)
                ) {
                    Text("Parcourir les offres")
                }
            }
        } else {
            // La même carte que dans la liste générale permet d'ouvrir l'offre ou retirer l'étoile.
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(favorites, key = { it.id }) { offer ->
                    OfferListCard(
                        offer = offer,
                        onClick = { onOpenOffer(offer.id) },
                        onFavorite = { viewModel.toggleFavorite(offer) }
                    )
                }
            }
        }
    }
}
