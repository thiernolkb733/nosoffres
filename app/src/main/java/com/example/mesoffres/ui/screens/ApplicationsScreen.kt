package com.example.mesoffres.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.data.model.JobApplication
import com.example.mesoffres.ui.util.parseFrenchDeadline
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val ApplicationsBackground = Color(0xFF191A18)
private val ApplicationsCard = Color(0xFF20211F)
private val ApplicationsOrange = Color(0xFFE86A3A)
private val ApplicationsMuted = Color(0xFFAAAAA6)
private val ApplicationsBorder = Color(0xFF333430)

/** Écran 3 — suivi des candidatures, rangées dans les cinq statuts du cahier des charges. */
@Composable
fun ApplicationsScreen(
    viewModel: EmploiViewModel,
    onOpenOffers: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    // Cette liste est observée dans Room ; une candidature ajoutée ou modifiée apparaît ici sans rechargement.
    val applications by viewModel.applications.collectAsStateWithLifecycle()

    // L'ordre est fixé pour que les candidatures soient toujours présentées de la même manière.
    val statusSections = listOf(
        ApplicationStatusSection("À envoyer", Color(0xFFE9A33A)),
        ApplicationStatusSection("Envoyée", Color(0xFF65A5E8)),
        ApplicationStatusSection("Entretien", Color(0xFFE7B644)),
        ApplicationStatusSection("Acceptée", Color(0xFF75C05B)),
        ApplicationStatusSection("Refusée", Color(0xFFE18A8E))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApplicationsBackground)
    ) {
        // Bandeau orange commun avec l'écran Offres pour garder une identité visuelle cohérente.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ApplicationsOrange)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            Text(
                text = "Mes candidatures",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // LazyColumn crée les sections à la demande, ce qui reste efficace quand la liste grandit.
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            items(statusSections, key = { it.label }) { section ->
                // On sélectionne seulement les candidatures de la section courante.
                val sectionApplications = applications.filter { it.status == section.label }

                Column {
                    Text(
                        text = section.label,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = section.color,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    if (sectionApplications.isEmpty()) {
                        EmptyStatusCard()
                    } else {
                        // Les candidatures de cette catégorie sont dessinées sous son titre.
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            sectionApplications.forEach { application ->
                                ApplicationCard(
                                    application = application,
                                    statusColor = section.color,
                                    onStatusChange = { newStatus, followUpDate ->
                                        // Le ViewModel écrit le nouveau statut dans Room.
                                        viewModel.updateApplicationStatus(application.id, newStatus, followUpDate)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Barre conforme à la maquette. Le choix courant est Candidatures.
        ApplicationsBottomBar(
            onOpenOffers = onOpenOffers,
            onOpenApplications = {},
            onOpenDashboard = onOpenDashboard
        )
    }
}

/** Modèle d'affichage d'une section : son titre et sa couleur de statut. */
private data class ApplicationStatusSection(val label: String, val color: Color)

/** Affiche une candidature avec la même structure, quel que soit son statut. */
@Composable
private fun ApplicationCard(
    application: JobApplication,
    statusColor: Color,
    onStatusChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = ApplicationsCard)
    ) {
        // La barre colorée à gauche reprend le code couleur visible dans la maquette.
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .height(120.dp)
                    .background(statusColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 13.dp)
            ) {
                Text(
                    text = application.offerTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitleFor(application),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ApplicationsMuted
                )
                Spacer(Modifier.height(8.dp))
                StatusSelector(
                    currentStatus = application.status,
                    statusColor = statusColor,
                    onStatusChange = onStatusChange
                )
            }
        }
    }
}

/** Petit menu qui enregistre le changement et déplace ensuite la carte vers sa nouvelle section. */
@Composable
private fun StatusSelector(
    currentStatus: String,
    statusColor: Color,
    onStatusChange: (String, String) -> Unit
) {
    var expanded by rememberSaveable(currentStatus) { mutableStateOf(false) }
    var pendingSentConfirmation by rememberSaveable(currentStatus) { mutableStateOf(false) }
    var followUpDate by rememberSaveable(currentStatus) { mutableStateOf("") }
    val allStatuses = listOf("À envoyer", "Envoyée", "Entretien", "Acceptée", "Refusée")

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
            border = BorderStroke(1.dp, statusColor),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Statut : $currentStatus",
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Modifier le statut",
                tint = statusColor
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allStatuses.filterNot { it == currentStatus }.forEach { newStatus ->
                DropdownMenuItem(
                    text = { Text(newStatus) },
                    onClick = {
                        expanded = false
                        if (newStatus == "Envoyée") {
                            pendingSentConfirmation = true
                        } else {
                            onStatusChange(newStatus, "")
                        }
                    }
                )
            }
        }
    }

    // « Envoyée » décrit un envoi fait par l'utilisateur hors de l'application.
    if (pendingSentConfirmation) {
        AlertDialog(
            onDismissRequest = { pendingSentConfirmation = false },
            title = { Text("Confirmer l'envoi") },
            text = {
                Column {
                    Text("Avez-vous envoyé cette candidature à l'entreprise ? L'application met seulement à jour votre suivi local.")
                    OutlinedTextField(
                        value = followUpDate,
                        onValueChange = { followUpDate = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        label = { Text("Date de relance (facultative)") },
                        placeholder = { Text("ex. 4 oct. 2026") },
                        supportingText = {
                            if (followUpDate.isNotBlank() && parseFrenchDeadline(followUpDate) == null) {
                                Text("Saisissez une date comme 4 oct. 2026.")
                            }
                        },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStatusChange("Envoyée", followUpDate.trim())
                        pendingSentConfirmation = false
                    },
                    enabled = followUpDate.isBlank() || parseFrenchDeadline(followUpDate) != null
                ) {
                    Text("Oui, elle est envoyée")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingSentConfirmation = false }) {
                    Text("Pas encore")
                }
            }
        )
    }
}

/** Le sous-titre présente une date seulement quand elle correspond à l'information disponible. */
private fun subtitleFor(application: JobApplication): String = when (application.status) {
    "Envoyée" -> buildString {
        append("${application.company} · envoyée le ${application.dateApplied}")
        if (application.followUpDate.isNotBlank()) {
            append(" · relance le ${application.followUpDate}")
        }
    }
    "Entretien" -> "${application.company} · ${application.city}"
    "Acceptée" -> "${application.company} · ${application.city}"
    "Refusée" -> if (application.notes.isBlank()) application.company
    else "${application.company} · ${application.notes}"
    else -> "${application.company} · ${application.city}"
}

/** État vide identique pour chaque catégorie sans candidature. */
@Composable
private fun EmptyStatusCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ApplicationsBackground,
        border = BorderStroke(1.dp, ApplicationsBorder)
    ) {
        Text(
            text = "Aucune donnée pour le moment.",
            style = MaterialTheme.typography.bodyLarge,
            color = ApplicationsMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp)
        )
    }
}

/** Navigation entre les trois écrans principaux du MVP. */
@Composable
private fun ApplicationsBottomBar(
    onOpenOffers: () -> Unit,
    onOpenApplications: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    NavigationBar(containerColor = ApplicationsBackground) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = ApplicationsOrange,
            selectedTextColor = ApplicationsOrange,
            indicatorColor = ApplicationsBackground,
            unselectedIconColor = ApplicationsMuted,
            unselectedTextColor = ApplicationsMuted
        )

        NavigationBarItem(
            selected = false,
            onClick = onOpenOffers,
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Offres", style = MaterialTheme.typography.bodyMedium) },
            colors = colors
        )
        NavigationBarItem(
            selected = true,
            onClick = onOpenApplications,
            icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null) },
            label = { Text("Candidatures", style = MaterialTheme.typography.bodyMedium) },
            colors = colors
        )
        NavigationBarItem(
            selected = false,
            onClick = onOpenDashboard,
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Tableau de bord", style = MaterialTheme.typography.bodyMedium) },
            colors = colors
        )
    }
}
