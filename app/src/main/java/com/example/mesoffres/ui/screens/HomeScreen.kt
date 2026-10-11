package com.example.mesoffres.ui.screens


import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.ui.util.UpcomingDeadline
import com.example.mesoffres.ui.util.buildUpcomingDeadlines
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val DashboardBackground = Color(0xFF191A18)
private val DashboardSurface = Color(0xFF20211F)
private val DashboardOrange = Color(0xFFE86A3A)
private val DashboardMuted = Color(0xFFAAAAA6)

/** Écran 4 — résumé des candidatures et prochaines dates à ne pas oublier. */
@Composable
fun HomeScreen(
    viewModel: EmploiViewModel,
    onOpenOffers: () -> Unit,
    onOpenApplications: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenProfile: () -> Unit
) {
    // Les deux flux sont fournis par Room ; les chiffres changent après une action dans l'application.
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val activeSearch by viewModel.offerSearchQuery.collectAsStateWithLifecycle()
    var searchDraft by rememberSaveable { mutableStateOf(activeSearch) }
    val focusManager = LocalFocusManager.current

    // Si l'utilisateur revient du catalogue, on reflète la dernière recherche dans ce champ.
    LaunchedEffect(activeSearch) {
        searchDraft = activeSearch
    }

    // Une seule fonction sert au bouton et à la touche « Rechercher » du clavier.
    val submitSearch = {
        viewModel.updateOfferSearchQuery(searchDraft)
        focusManager.clearFocus()
        onOpenOffers()
    }

    // Chaque indicateur compte le statut correspondant parmi toutes les candidatures enregistrées.
    val statusCounts = listOf(
        DashboardStatus("À envoyer", applications.count { it.status == "À envoyer" }, Color(0xFFE9A33A)),
        DashboardStatus("Envoyées", applications.count { it.status == "Envoyée" }, Color(0xFF65A5E8)),
        DashboardStatus("Entretiens", applications.count { it.status == "Entretien" }, Color(0xFFE7B644)),
        DashboardStatus("Acceptées", applications.count { it.status == "Acceptée" }, Color(0xFF75C05B)),
        DashboardStatus("Refusées", applications.count { it.status == "Refusée" }, Color(0xFFE18A8E))
    )

    // On prépare la liste une fois à partir des offres et candidatures observées.
    val upcomingDeadlines = remember(offers, applications) {
        buildUpcomingDeadlines(offers, applications)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
    ) {
        // Le bandeau reprend le titre orange de la maquette.
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardOrange)
                .statusBarsPadding()
                .padding(start = 24.dp, end = 14.dp, top = 16.dp, bottom = 16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tableau de bord",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = onOpenProfile) {
                Icon(Icons.Default.Person, contentDescription = "Ouvrir le profil", tint = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchDraft,
                    onValueChange = { searchDraft = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rechercher un poste ou une entreprise") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = submitSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Rechercher les offres")
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = DashboardOrange,
                        unfocusedBorderColor = Color(0xFF333430),
                        focusedLabelColor = DashboardOrange,
                        unfocusedLabelColor = DashboardMuted,
                        cursorColor = DashboardOrange,
                        focusedContainerColor = DashboardSurface,
                        unfocusedContainerColor = DashboardSurface
                    )
                )
            }

            item {
                // Deux cartes par ligne rendent les nombres lisibles sur un écran de téléphone.
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    statusCounts.chunked(2).forEach { rowStatuses ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowStatuses.forEach { status ->
                                DashboardStat(status = status, modifier = Modifier.weight(1f))
                            }
                            // Le dernier statut reste aligné sur la première colonne.
                            if (rowStatuses.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Prochaines échéances",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            if (upcomingDeadlines.isEmpty()) {
                item {
                    Text(
                        text = "Aucune échéance à venir.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DashboardMuted
                    )
                }
            } else {
                // Les dates sont classées de la plus proche à la plus éloignée.
                items(upcomingDeadlines, key = { "${it.company}-${it.title}-${it.date}" }) { deadline ->
                    DeadlineCard(deadline = deadline, onClick = onOpenReminders)
                }
            }
        }

        DashboardBottomBar(
            onOpenOffers = onOpenOffers,
            onOpenApplications = onOpenApplications,
            onOpenDashboard = {} // Le tableau de bord est déjà l'écran affiché.
        )
    }
}

/** Données d'affichage d'une carte de statistique. */
private data class DashboardStatus(val label: String, val count: Int, val color: Color)

/** Une échéance présentée sur le tableau de bord avec sa date et sa provenance. */
/** Carte d'indicateur avec nombre et statut correspondant. */
@Composable
private fun DashboardStat(status: DashboardStatus, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp)
        ) {
            Text(
                text = status.label,
                style = MaterialTheme.typography.titleMedium,
                color = DashboardMuted
            )
            Text(
                text = status.count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = status.color
            )
        }
    }
}

/** Carte d'échéance ; toucher la carte ouvre la liste détaillée des rappels. */
@Composable
private fun DeadlineCard(deadline: UpcomingDeadline, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Date limite",
                tint = DashboardOrange
            )
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = deadline.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "${deadline.kind} · ${deadline.company} · ${deadline.dateLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DashboardMuted
                )
            }
        }
    }
}

/** Barre de navigation principale avec le tableau de bord sélectionné. */
@Composable
private fun DashboardBottomBar(
    onOpenOffers: () -> Unit,
    onOpenApplications: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    NavigationBar(containerColor = DashboardBackground) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = DashboardOrange,
            selectedTextColor = DashboardOrange,
            indicatorColor = DashboardBackground,
            unselectedIconColor = DashboardMuted,
            unselectedTextColor = DashboardMuted
        )

        NavigationBarItem(
            selected = false,
            onClick = onOpenOffers,
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Offres", style = MaterialTheme.typography.bodyMedium) },
            colors = colors
        )
        NavigationBarItem(
            selected = false,
            onClick = onOpenApplications,
            icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null) },
            label = { Text("Candidatures", style = MaterialTheme.typography.bodyMedium) },
            colors = colors
        )
        NavigationBarItem(
            selected = true,
            onClick = onOpenDashboard,
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Tableau de bord", style = MaterialTheme.typography.bodyMedium) },
            colors = colors
        )
    }
}
