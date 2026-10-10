package com.example.mesoffres.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mesoffres.data.model.Offer

private val CardSurface = Color(0xFF1B1C1A)
private val CardMuted = Color(0xFFAAAAA6)
private val CardBorder = Color(0xFF333430)
private val CardOrange = Color(0xFFE86A3A)

/** Carte partagée par la liste d'offres et la liste des favoris. */
@Composable
fun OfferListCard(
    offer: Offer,
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = CardSurface,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(Modifier.padding(start = 18.dp, top = 16.dp, end = 10.dp, bottom = 16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(3.dp))
                    Text("${offer.company} · ${offer.city}", color = CardMuted)
                }

                // L'état de l'étoile vient de Room ; un appui le met à jour dans le ViewModel.
                IconButton(onClick = onFavorite, modifier = Modifier.size(42.dp)) {
                    Icon(
                        imageVector = if (offer.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (offer.isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                        tint = if (offer.isFavorite) CardOrange else CardMuted
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OfferTag(
                    text = offer.type,
                    background = if (offer.type.equals("Stage", ignoreCase = true)) Color(0xFF492012) else Color(0xFF102E4A),
                    foreground = if (offer.type.equals("Stage", ignoreCase = true)) Color(0xFFFF956B) else Color(0xFF80B8F4)
                )
                OfferTag(text = offer.sector, background = Color(0xFF131412), foreground = Color(0xFFE0E0DC))
            }

            Spacer(Modifier.height(10.dp))
            Text("Échéance ${offer.deadline}", color = CardMuted)
        }
    }
}

/** Badge compact pour le type et le secteur d'une offre. */
@Composable
private fun OfferTag(text: String, background: Color, foreground: Color) {
    Surface(color = background, shape = RoundedCornerShape(9.dp)) {
        Text(
            text = text,
            color = foreground,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
        )
    }
}
