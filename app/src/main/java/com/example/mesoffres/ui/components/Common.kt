package com.example.mesoffres.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mesoffres.data.model.Offer
import com.example.mesoffres.ui.theme.PrimaryBlue

/**
 * Barre supérieure réutilisable.
 */
@Composable
fun AppTopBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

/**
 * Petit badge utilisé pour afficher le type d'offre ou un statut.
 */
@Composable
fun StatusChip(
    text: String,
    color: Color = PrimaryBlue
) {
    AssistChip(
        onClick = {},
        label = { Text(text) }
    )
}

/**
 * Carte complète d'une offre.
 */
@Composable
fun OfferCard(
    offer: Offer,
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                // Faux logo simple pour éviter d'ajouter des images externes.
                Row(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = offer.company,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector =
                            if (offer.isFavorite)
                                Icons.Default.Bookmark
                            else
                                Icons.Default.BookmarkBorder,
                        contentDescription = "Favori"
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(offer.city)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(offer.type)
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(offer.sector)

                StatusChip(
                    text = offer.deadline,
                    color = Color.Red
                )
            }
        }
    }
}
