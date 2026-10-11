package com.example.mesoffres.ui.screens

import androidx.compose.foundation.layout.statusBarsPadding
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val DetailBackground = Color(0xFF191A18)
private val DetailOrange = Color(0xFFE86A3A)
private val DetailMuted = Color(0xFFAAAAA6)
private val DetailBorder = Color(0xFF393A36)
private val DeadlinePink = Color(0xFFE79A9D)

/** Écran 2 — toutes les informations utiles avant de postuler à une offre. */
@Composable
fun OfferDetailScreen(
    offerId: Long,
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onApply: () -> Unit,
    onEdit: (Long) -> Unit
) {
    // La liste observée vient de Room via le ViewModel : un changement de favori est reflété ici.
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val offer = offers.firstOrNull { it.id == offerId }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
    ) {
        // Barre orange avec retour, comme dans la capture du détail.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DetailOrange)
                .statusBarsPadding()
                .padding(
                    start = 0.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Détail de l'offre",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        // Tant que l'offre n'est pas trouvée, on explique la situation au lieu de montrer un écran vide.
        if (offer == null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cette offre n'est plus disponible.", color = Color.White)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onBack) {
                    Text("Retour aux offres")
                }
            }
        } else {
            // Seule la partie descriptive défile : les deux actions restent visibles en bas.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp)
            ) {
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = offer.company,
                    style = MaterialTheme.typography.titleLarge,
                    color = DetailMuted
                )

                Spacer(Modifier.height(18.dp))

                // Type, secteur et ville sont des champs de l'entité Offer sauvegardée localement.
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailTag(offer.type, background = Color(0xFF492012), foreground = Color(0xFFFF956B))
                    DetailTag(offer.sector, background = Color(0xFF131412), foreground = Color.White)
                    DetailTag(offer.city, background = Color(0xFF131412), foreground = Color.White)
                }

                Spacer(Modifier.height(22.dp))

                // La maquette montre une rémunération, mais le modèle Room défini dans le cahier des charges
                // ne possède pas encore ce champ : on n'invente donc pas de montant à l'écran.
                DeadlinePanel(deadline = offer.deadline)

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = offer.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFE0E0DC)
                )

                Spacer(Modifier.height(24.dp))

                // Toucher l'adresse prépare un courriel dans l'application de messagerie du téléphone.
                Row(
                    modifier = Modifier.clickable {
                        openContactEmail(context, offer.contact, offer.title)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Adresse de contact",
                        tint = Color(0xFFE0E0DC)
                    )
                    Text(
                        text = offer.contact,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            // Les actions sont fixées en bas pour rester accessibles après lecture de la description.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DetailBackground)
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onEdit(offer.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        text = "Modifier",
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.toggleFavorite(offer) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    border = BorderStroke(1.dp, DetailBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = if (offer.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(" Favori", color = Color.White)
                }

                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DetailOrange),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Postuler", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/** Bloc teinté qui attire l'attention sur la date limite de candidature. */
@Composable
private fun DeadlinePanel(deadline: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF35180F),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = DeadlinePink)
                Text(
                    text = "À candidater avant le $deadline",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeadlinePink,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

/** Badge réutilisable pour les métadonnées de l'offre. */
@Composable
private fun DetailTag(text: String, background: Color, foreground: Color) {
    Surface(color = background, shape = RoundedCornerShape(10.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = foreground,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

/** Ouvre un brouillon adressé au contact ; le message n'est jamais envoyé automatiquement. */
private fun openContactEmail(context: Context, address: String, offerTitle: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$address")).apply {
        putExtra(Intent.EXTRA_SUBJECT, "Question sur l'offre : $offerTitle")
    }

    try {
        context.startActivity(emailIntent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "Aucune application de messagerie n'est installée.", Toast.LENGTH_SHORT).show()
    }
}
